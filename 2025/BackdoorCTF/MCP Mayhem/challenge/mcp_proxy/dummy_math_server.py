from mcp.server.fastmcp import FastMCP
import argparse
import uvicorn

parser = argparse.ArgumentParser()
parser.add_argument("--name", type=str, default="Generic", help="Name of this server instance")
parser.add_argument("--port", type=int, default=8001, help="Port to run the server on")
args = parser.parse_args()

mcp = FastMCP(f"Math-{args.name}")

@mcp.tool()
def add(a: int, b: int) -> int:
    """Adds two numbers"""
    try:
        return a + b
    except Exception as e:
        print(f"Error: {e}")
        return f"{a}+{b} gave error: {e}"


@mcp.tool()
def multiply(a: int, b: int) -> int:
    """Multiplies two numbers"""
    try:
        return a * b
    except Exception as e:
        print(f"Error: {e}")
        return f"{a}*{b} gave error: {e}"

@mcp.tool()
def identity() -> str:
    """Returns the identity of this server"""
    return f"I am Server {args.name}"

if __name__ == "__main__":
    import asyncio
    
    config = uvicorn.Config(
        app=mcp.sse_app,
        host="0.0.0.0",
        port=args.port,
        log_level="info"
    )
    server = uvicorn.Server(config)
    asyncio.run(server.serve())