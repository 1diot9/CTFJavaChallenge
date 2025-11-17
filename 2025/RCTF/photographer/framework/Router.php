<?php
class Router {
    private $routes = [];
    private $notFoundHandler;
    
    public function get($path, $handler) {
        $this->addRoute('GET', $path, $handler);
    }
    
    public function post($path, $handler) {
        $this->addRoute('POST', $path, $handler);
    }
    
    private function addRoute($method, $path, $handler) {
        $this->routes[] = [
            'method' => $method,
            'path' => $path,
            'handler' => $handler
        ];
    }
    
    public function notFound($handler) {
        $this->notFoundHandler = $handler;
    }
    
    public function dispatch() {
        $method = $_SERVER['REQUEST_METHOD'];
        $uri = parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH);
        
        foreach ($this->routes as $route) {
            if ($route['method'] !== $method) {
                continue;
            }
            
            $pattern = $this->convertToRegex($route['path']);
            
            if (preg_match($pattern, $uri, $matches)) {
                array_shift($matches);
                
                $handler = $route['handler'];
                
                if (is_callable($handler)) {
                    call_user_func_array($handler, $matches);
                } elseif (is_string($handler)) {
                    list($controller, $method) = explode('@', $handler);
                    $controllerInstance = new $controller();
                    call_user_func_array([$controllerInstance, $method], $matches);
                }
                return;
            }
        }
        
        if ($this->notFoundHandler) {
            call_user_func($this->notFoundHandler);
        } else {
            http_response_code(404);
            echo "404 Not Found";
        }
    }
    
    private function convertToRegex($path) {
        $pattern = preg_replace('/\{([a-zA-Z0-9_]+)\}/', '([^/]+)', $path);
        return '#^' . $pattern . '$#';
    }
}

