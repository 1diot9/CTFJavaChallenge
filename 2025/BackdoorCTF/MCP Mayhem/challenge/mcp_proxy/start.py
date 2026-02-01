import os

os.system("python dummy_math_server.py --name Alpha --port 8001 &")
print("Started Alpha math server on port 8001")
os.system("python dummy_math_server.py --name Beta --port 8002 &")
print("Started Beta math server on port 8002")

print("Running MCP Proxy...")
os.system("python mcp_proxy.py")
