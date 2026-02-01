import os
from flask import Blueprint, render_template, request, jsonify
import asyncio
import sys
from mcp.client.sse import sse_client
from mcp.client.session import ClientSession
import mcp.types as types

async def handle_elicitation(context, params: types.ElicitRequestParams) -> types.ElicitResult:
    print(f"[Client] Received elicitation request: {params.message}", file=sys.stderr)
    
    import re
    match = re.search(r'uploads/([^":\s]+)', params.message)
    if match:
        file_path = f"uploads/{match.group(1)}"
        print(f"[Client] Reading file: {file_path}", file=sys.stderr)
        try:
            with open(file_path, 'r') as f:
                file_contents = f.read()
            
            print(f"[Client] Read file {file_path}: {len(file_contents)} bytes", file=sys.stderr)
            
            return types.ElicitResult(
                action="accept",
                content={
                    "content": file_contents
                }
            )
        except FileNotFoundError:
            print(f"[Client] File not found: {file_path}", file=sys.stderr)
            return types.ElicitResult(
                action="decline",
                content={"error": f"File not found: {file_path}"}
            )
        except Exception as e:
            print(f"[Client] Error reading file: {e}", file=sys.stderr)
            return types.ElicitResult(
                action="decline",
                content={"error": str(e)}
            )
    
    print(f"[Client] Could not parse elicitation request", file=sys.stderr)
    return types.ElicitResult(
        action="decline",
        content={"error": "Unable to handle this elicitation request"}
    )

async def run_client_async(tool_name: str, arguments: dict):
    proxy_url = os.environ.get("MCP_PROXY_URL", "http://localhost:8000") + "/sse"

    async with sse_client(proxy_url) as (read, write):
        async with ClientSession(
            read, 
            write,
            elicitation_callback=handle_elicitation
        ) as session:
            await session.initialize()
            print(f"[Client] Initialized with capabilities", file=sys.stderr)
            try:
                result = await session.call_tool(tool_name, arguments)
            except Exception as e:
                print(f"[Client] Error calling tool: {e}", file=sys.stderr)
                return {"error": str(e)}

            return result

def run_client(tool_name: str, arguments: dict):
    return asyncio.run(run_client_async(tool_name, arguments))

client_bp = Blueprint('client', __name__, subdomain="client")

@client_bp.route("/call", methods=["GET", "POST"])
def private():
    if request.method == "POST":
        tool = request.json.get("tool")
        arguments = request.json.get("arguments", {})

        result = run_client(tool, arguments)
        
        if isinstance(result, dict) and "error" in result:
            return jsonify(result)
        
        if hasattr(result, 'content'):
            content_list = result.content
            
            if hasattr(result, 'structuredContent') and result.structuredContent:
                return jsonify(result.structuredContent)
            
            if content_list:
                extracted_content = []
                for item in content_list:
                    if hasattr(item, 'text'):
                        extracted_content.append(item.text)
                    else:
                        extracted_content.append(str(item))
                
                is_error = hasattr(result, 'isError') and result.isError
                
                if len(extracted_content) == 1:
                    response_data = {"result": extracted_content[0]}
                else:
                    response_data = {"result": extracted_content}
                
                if is_error:
                    response_data["error"] = True
                    
                return jsonify(response_data)
            
            return jsonify({"result": None})
        
        return jsonify({"result": str(result)})

    return render_template("client/call.html")
