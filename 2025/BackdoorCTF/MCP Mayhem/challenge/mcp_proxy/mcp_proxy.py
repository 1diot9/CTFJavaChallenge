import sys
import json
import os
from contextlib import AsyncExitStack
from typing import Dict, Any, List, Optional

from mcp.server import Server, NotificationOptions
from mcp.server.sse import SseServerTransport
from mcp.client.sse import sse_client
from mcp.client.session import ClientSession
import mcp.types as types
from starlette.applications import Starlette
from starlette.routing import Route, Mount
from starlette.responses import Response, JSONResponse
import uvicorn
from starlette.requests import Request

from utils import decodeCookie

UPSTREAM_SERVERS = {
    "math_alpha": {
        "url": "http://localhost:8001/sse"
    },
    "math_beta": {
        "url": "http://localhost:8002/sse"
    },
}

ADMIN_TOKEN = os.environ.get("ADMIN_TOKEN", "secret_admin_token_12345")

ALLOWED_TOOLS_LIST = [
    "math_alpha_add",
    "math_alpha_multiply",
    "math_alpha_identity",
    "math_beta_add",
    "math_beta_multiply",
    "math_beta_identity",
]

class MCPProxy:
    def __init__(self):
        self.server = Server("MCP-Aggregator-Proxy")
        self.sessions: Dict[str, ClientSession] = {}
        self.exit_stack = AsyncExitStack()
        self.client_session = None 
        
        print(f"[Proxy] Initialized MCP Proxy", file=sys.stderr)

        self.server.list_tools()(self.handle_list_tools)
        self.server.call_tool()(self.handle_call_tool)
        self.server.list_resources()(self.handle_list_resources)
        self.server.read_resource()(self.handle_read_resource)
        self.server.list_prompts()(self.handle_list_prompts)
        self.server.get_prompt()(self.handle_get_prompt)
    
    async def handle_elicitation_from_upstream(self, context, params: types.ElicitRequestParams) -> types.ElicitResult:
        """
        Handle elicitation requests from upstream servers.
        This forwards the request to the CLIENT.
        """
        print(f"[Proxy] Received elicitation request from upstream server", file=sys.stderr)
        print(f"[Proxy] Message: {params.message}", file=sys.stderr)
        print(f"[Proxy] Requested schema: {params.requestedSchema}", file=sys.stderr)
        
        if not self.client_session:
            print(f"[Proxy] ERROR: No client session available", file=sys.stderr)
            return types.ElicitResult(
                action="decline",
                content={"error": "No client session available"}
            )
        
        try:
            result = await self.client_session.elicit(
                message=params.message,
                requestedSchema=params.requestedSchema
            )
            print(f"[Proxy] Received response from client: {result.action}", file=sys.stderr)
            
            return result
            
        except Exception as e:
            print(f"[Proxy] Error forwarding elicitation to client: {e}", file=sys.stderr)
            import traceback
            traceback.print_exc()
            return types.ElicitResult(
                action="decline",
                content={"error": str(e)}
            )

    async def connect_upstreams(self):
        """Connects to all defined upstream servers via HTTP/SSE."""
        for name, config in UPSTREAM_SERVERS.items():
            try:
                url = config["url"]
                
                sse_read, sse_write = await self.exit_stack.enter_async_context(
                    sse_client(url)
                )
                
                session = await self.exit_stack.enter_async_context(
                    ClientSession(
                        sse_read, 
                        sse_write,
                        elicitation_callback=self.handle_elicitation_from_upstream
                    )
                )
                
                await session.initialize()
                
                self.sessions[name] = session
                print(f"[Proxy] Connected to upstream: {name} at {url}", file=sys.stderr)
                
            except Exception as e:
                print(f"[Proxy] Failed to connect to {name}: {e}", file=sys.stderr)
                import traceback
                traceback.print_exc()

    async def add_upstream_server(self, name: str, url: str) -> bool:
        """Dynamically add a new upstream MCP server."""
        if name in self.sessions:
            print(f"[Proxy] Server {name} already exists", file=sys.stderr)
            return False
        
        try:
            UPSTREAM_SERVERS[name] = {"url": url}
            
            sse_read, sse_write = await self.exit_stack.enter_async_context(
                sse_client(url)
            )
            
            session = await self.exit_stack.enter_async_context(
                ClientSession(
                    sse_read, 
                    sse_write,
                    elicitation_callback=self.handle_elicitation_from_upstream
                )
            )
            
            await session.initialize()
            self.sessions[name] = session
            
            print(f"[Proxy] Successfully added upstream server: {name} at {url}", file=sys.stderr)
            return True
            
        except Exception as e:
            print(f"[Proxy] Failed to add upstream server {name}: {e}", file=sys.stderr)
            import traceback
            traceback.print_exc()
            if name in UPSTREAM_SERVERS:
                del UPSTREAM_SERVERS[name]
            return False


    async def handle_list_tools(self, *args, **kwargs) -> List[types.Tool]:
        if self.server.request_context:
            self.client_session = self.server.request_context.session
            print(f"[Proxy] Captured client session reference", file=sys.stderr)
        
        print(f"[Proxy] Returning hardcoded tool list (hiding any dynamic servers)", file=sys.stderr)
        
        filtered_tools = []
        
        for server_name, session in self.sessions.items():
            try:
                result = await session.list_tools()
                for tool in result.tools:
                    full_tool_name = f"{server_name}_{tool.name}"
                    
                    if full_tool_name in ALLOWED_TOOLS_LIST:
                        tool.name = full_tool_name
                        filtered_tools.append(tool)
                        print(f"[Proxy] Including allowed tool: {full_tool_name}", file=sys.stderr)
                    else:
                        print(f"[Proxy] Hiding tool: {full_tool_name} (not in allowed list)", file=sys.stderr)
            except Exception as e:
                print(f"[Proxy] Error fetching tools from {server_name}: {e}", file=sys.stderr)
        
        print(f"[Proxy] Returning {len(filtered_tools)} tools from allowed list", file=sys.stderr)
        return filtered_tools

    async def handle_call_tool(
        self, 
        name: str, 
        arguments: Optional[Dict[str, Any]] = None
    ) -> types.CallToolResult:
        print(f"[Proxy] Tool call allowed: {name}", file=sys.stderr)
        
        server_name = None
        original_tool_name = None
        print(f"[Proxy] Server names: {', '.join(self.sessions.keys())}", file=sys.stderr)
        
        for srv_name in self.sessions.keys():
            prefix = f"{srv_name}_"
            if name.startswith(prefix):
                server_name = srv_name
                original_tool_name = name[len(prefix):]
                break
        
        if server_name is None:
            raise ValueError(f"Invalid tool name format: {name}. Could not identify server.")
        
        if server_name not in self.sessions:
            raise ValueError(f"Unknown server: {server_name}")

        session = self.sessions[server_name]
        result = await session.call_tool(original_tool_name, arguments)
        
        return result


    async def handle_list_resources(self, *args, **kwargs) -> List[types.Resource]:
        all_resources = []
        for server_name, session in self.sessions.items():
            try:
                result = await session.list_resources()
                for resource in result.resources:
                    all_resources.append(resource)
            except Exception as e:
                print(f"Error resources {server_name}: {e}", file=sys.stderr)
        return all_resources

    async def handle_read_resource(self, uri: str) -> List[types.TextContent]:
        for name, session in self.sessions.items():
            try:
                return await session.read_resource(uri)
            except Exception:
                continue
        raise ValueError(f"Resource not found on any upstream server: {uri}")

    async def handle_list_prompts(self, *args, **kwargs) -> List[types.Prompt]:
        all_prompts = []
        for server_name, session in self.sessions.items():
            try:
                result = await session.list_prompts()
                for prompt in result.prompts:
                    prompt.name = f"{server_name}_{prompt.name}"
                    all_prompts.append(prompt)
            except Exception:
                pass
        return all_prompts

    async def handle_get_prompt(self, name: str, arguments: Optional[Dict[str, str]] = None) -> types.GetPromptResult:
        server_name = None
        original_name = None
        
        for srv_name in self.sessions.keys():
            prefix = f"{srv_name}_"
            if name.startswith(prefix):
                server_name = srv_name
                original_name = name[len(prefix):]
                break
        
        if server_name is None:
            raise ValueError(f"Invalid prompt name: {name}. Could not identify server.")

        if server_name not in self.sessions:
            raise ValueError(f"Unknown server: {server_name}")

        session = self.sessions[server_name]
        return await session.get_prompt(original_name, arguments)

    async def run(self):
        """Run the proxy server with HTTP/SSE transport."""
        async with self.exit_stack:
            await self.connect_upstreams()
            print("[Proxy] Proxy Server Running on http://localhost:8000", file=sys.stderr)

def create_app(proxy: MCPProxy):
    """Create Starlette application for the proxy."""
    sse = SseServerTransport("/messages")
    
    async def handle_sse_endpoint(request):
        """Handle SSE endpoint for client connections."""
        if not proxy.sessions:
            await proxy.connect_upstreams()
        
        async with sse.connect_sse(
            request.scope, request.receive, request._send
        ) as streams:
            await proxy.server.run(
                streams[0], streams[1], proxy.server.create_initialization_options()
            )
        return Response()
    
    async def handle_admin_add_server(request: Request):
        """Admin endpoint to add new MCP servers."""
        user_id = decodeCookie(request.cookies.get("token")).get('user_id')
        print("[Proxy] Admin add-server request from user_id:", user_id, file=sys.stderr)
        
        if user_id != 1:
            return JSONResponse(
                {"error": "Unauthorized", "message": "Invalid admin token"},
                status_code=403
            )
        
        try:
            body = await request.json()
            name = body.get("name")
            url = body.get("url")
            print("[Proxy] Admin request to add server:", name, url, file=sys.stderr)
            
            if not name or not url:
                return JSONResponse(
                    {"error": "Bad Request", "message": "Both 'name' and 'url' are required"},
                    status_code=400
                )
            
            success = await proxy.add_upstream_server(name, url)
            
            if success:
                return JSONResponse(
                    {
                        "success": True,
                        "message": f"Server '{name}' added successfully",
                        "name": name,
                        "url": url
                    },
                    status_code=201
                )
            else:
                return JSONResponse(
                    {
                        "error": "Conflict",
                        "message": f"Server '{name}' already exists or failed to connect"
                    },
                    status_code=409
                )
                
        except json.JSONDecodeError:
            return JSONResponse(
                {"error": "Bad Request", "message": "Invalid JSON"},
                status_code=400
            )
        except Exception as e:
            print(f"[Proxy] Error in admin endpoint: {e}", file=sys.stderr)
            import traceback
            traceback.print_exc()
            return JSONResponse(
                {"error": "Internal Server Error", "message": str(e)},
                status_code=500
            )

    proxy.run()

    app = Starlette(
        routes=[
            Route("/sse", handle_sse_endpoint, methods=["GET"]),
            Route("/admin/add-server", handle_admin_add_server, methods=["POST"]),
            Mount("/messages", sse.handle_post_message),
        ]
    )
    
    return app

if __name__ == "__main__":
    proxy = MCPProxy()
    app = create_app(proxy)
    
    uvicorn.run(app, host="0.0.0.0", port=8000)