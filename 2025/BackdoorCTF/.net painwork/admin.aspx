<%@ Page Language="C#" AutoEventWireup="true" CodeBehind="admin.aspx.cs" Inherits="AdminPages.AdminPage" %>
<!DOCTYPE html>
<html>
<head runat="server">
    <meta charset="utf-8" />
    <title>Admin Healthcheck</title>
</head>
<body>
    
    <form id="form1" runat="server">
        <h2>Healthcheck (admin)</h2>
        <asp:HiddenField ID="csrf_token" runat="server" />
        URL: <input id="url" />
        <button type="button" id="run">Run</button>
        <pre id="out"></pre>

        <script type="text/javascript">
            function getCsrf() {
                
                if (typeof window.ANTI_XSRF_TOKEN !== 'undefined' && window.ANTI_XSRF_TOKEN) return window.ANTI_XSRF_TOKEN;
                var el = document.getElementById('<%= csrf_token.ClientID %>');
                if (el) return el.value;
                return null;
            }

            document.getElementById('run').onclick = async function () {
                var url = document.getElementById('url').value;
                var token = getCsrf();
                
                var r = await fetch('/health.ashx', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-AntiXsrf': token || ''
                    },
                    body: JSON.stringify({ url: url })
                });
                var text = await r.text();
                try {
                    document.getElementById('out').textContent = JSON.stringify(JSON.parse(text), null, 2);
                } catch (e) {
                    document.getElementById('out').textContent = text;
                }
            };
        </script>
    </form>
</body>
</html>