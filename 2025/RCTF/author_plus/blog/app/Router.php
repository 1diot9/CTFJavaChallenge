<?php
class Router {
    private $routes = [];
    
    public function add($method, $path, $handler) {
        $this->routes[] = [
            'method' => $method,
            'path' => $path,
            'handler' => $handler
        ];
    }
    
    public function dispatch() {
        $method = $_SERVER['REQUEST_METHOD'];
        $uri = parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH);

        
        foreach ($this->routes as $route) {
            if ($route['method'] === $method) {
                $pattern = $this->convertToRegex($route['path']);
                if (preg_match($pattern, $uri, $matches)) {
                    array_shift($matches); // Remove full match
                    $this->callHandler($route['handler'], $matches);
                    return;
                }
            }
        }
        
        // 404 page
        http_response_code(404);
        echo "404 - Page Not Found";
    }
    
    private function convertToRegex($path) {
        $pattern = preg_replace('/\{(\w+)\}/', '([^/]+)', $path);
        return '#^' . $pattern . '$#';
    }
    
    private function callHandler($handler, $params) {
        list($controller, $method) = explode('@', $handler);
        
        if (class_exists($controller)) {
            $controllerInstance = new $controller();
            if (method_exists($controllerInstance, $method)) {
                call_user_func_array([$controllerInstance, $method], $params);
            }
        }
    }
}

