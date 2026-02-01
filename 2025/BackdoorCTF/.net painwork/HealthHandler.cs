using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Threading.Tasks;
using System.Web;
using System.Web.Script.Serialization;
using System.Web.SessionState;

namespace Health.Handlers
{

    public class HeaderPair
    {
        public string name { get; set; }
        public string value { get; set; }
        public HeaderPair() { }
        public HeaderPair(string n, string v) { name = n; value = v; }
    }

    public class HealthcheckResponse
    {
        public bool ok { get; set; }
        public int statusCode { get; set; }
        public List<HeaderPair> headers { get; set; }
        public string snippet { get; set; }
        public string error { get; set; }

        public HealthcheckResponse(bool ok, int statusCode, List<HeaderPair> headers, string snippet, string error = null)
        {
            this.ok = ok;
            this.statusCode = statusCode;
            this.headers = headers ?? new List<HeaderPair>();
            this.snippet = snippet;
            this.error = error;
        }
    }

   
    public class HealthHandler : IHttpAsyncHandler, IRequiresSessionState
    {
        public bool IsReusable => false;

       
        public IAsyncResult BeginProcessRequest(HttpContext context, AsyncCallback cb, object extraData)
        {
           
            var task = ProcessRequestAsync(context);

            if (cb != null)
            {
                task.ContinueWith(_ => cb(task));
            }

            return task;
        }

        
        public void EndProcessRequest(IAsyncResult result)
        {
            
            (result as Task)?.Wait();
        }

        
        public async Task ProcessRequestAsync(HttpContext context)
        {
            context.Response.ContentType = "application/json";

            
            string cookieToken = context.Request.Cookies["__AntiXsrfToken"]?.Value;
            string headerToken = context.Request.Headers["X-AntiXsrf"];
            string sessionToken = context.Session["ANTI_XSRF"] as string;

            if (string.IsNullOrEmpty(cookieToken) ||
                string.IsNullOrEmpty(headerToken) ||
                string.IsNullOrEmpty(sessionToken) ||
                cookieToken != headerToken ||
                sessionToken != cookieToken)
            {
                context.Response.StatusCode = 403;
                context.Response.ContentType = "text/plain";
                context.Response.Write("Anti-XSRF validation failed");
                return;
            }




            string body;
            using (var sr = new StreamReader(context.Request.InputStream, context.Request.ContentEncoding))
            {
                
                body = await sr.ReadToEndAsync();
            }

            string targetUrl = null;
            try
            {
                var js = new JavaScriptSerializer();
                var dict = js.Deserialize<Dictionary<string, object>>(body);
                if (dict != null && dict.ContainsKey("url"))
                    targetUrl = dict["url"]?.ToString();
            }
            catch {  }

            if (string.IsNullOrWhiteSpace(targetUrl) || !Uri.TryCreate(targetUrl, UriKind.Absolute, out var target))
            {
                context.Response.StatusCode = 400;
                WriteJson(context, new { ok = false, error = "missing or invalid url" });
                return;
            }

            var timeout = TimeSpan.FromSeconds(5);

            try
            {
                string content = "";
                var headers = new List<HeaderPair>();

                ServicePointManager.SecurityProtocol = SecurityProtocolType.Tls12;
                using (var client = new WebClient())
                {
                    client.Headers.Add(HttpRequestHeader.UserAgent, "AdminHealthCheck-WebClient/1.0");

                    var downloadTask = client.DownloadStringTaskAsync(target);

                    
                    bool finished = await TaskWhenAnyWithTimeout(downloadTask, timeout);

                    if (!finished)
                    {
                        try { client.CancelAsync(); } catch { }
                        throw new TimeoutException("request timed out");
                    }

                    
                    content = await downloadTask;

                    var respHeaders = client.ResponseHeaders;
                    if (respHeaders != null)
                    {
                        foreach (string key in respHeaders.AllKeys)
                        {
                            var val = respHeaders[key];
                            if (val != null && val.Length > 200) val = val.Substring(0, 197) + "...";
                            headers.Add(new HeaderPair(key ?? "(null)", val ?? ""));
                        }
                    }
                }

                var snippet = content.Length > 4096 ? content.Substring(0, 4093) + "..." : content;
                WriteJson(context, new HealthcheckResponse(true, 200, headers, snippet));
            }
            catch (TimeoutException tex)
            {
                context.Response.StatusCode = 504;
                WriteJson(context, new HealthcheckResponse(false, 0, new List<HeaderPair>(), "", tex.Message));
            }
            catch (WebException wex)
            {
                var hdrs = new List<HeaderPair>();
                int code = 0;
                if (wex.Response is HttpWebResponse httpResp)
                {
                    code = (int)httpResp.StatusCode;
                    foreach (string key in httpResp.Headers.AllKeys)
                    {
                        var v = httpResp.Headers[key];
                        if (v != null && v.Length > 200) v = v.Substring(0, 197) + "...";
                        hdrs.Add(new HeaderPair(key ?? "(null)", v ?? ""));
                    }
                }
                WriteJson(context, new HealthcheckResponse(false, code, hdrs, "", wex.Message));
            }
            catch (Exception ex)
            {
                WriteJson(context, new HealthcheckResponse(false, 0, new List<HeaderPair>(), "", ex.Message));
            }
        }

        private void WriteJson(HttpContext ctx, object obj)
        {
            var js = new JavaScriptSerializer();
            ctx.Response.Write(js.Serialize(obj));
        }

        
        private async Task<bool> TaskWhenAnyWithTimeout(Task t, TimeSpan timeout)
        {
            var delay = Task.Delay(timeout);
            var finished = await Task.WhenAny(t, delay).ConfigureAwait(false);
            return finished == t;
        }

        public void ProcessRequest(HttpContext context)
        {
            throw new NotImplementedException();
        }
    }
}