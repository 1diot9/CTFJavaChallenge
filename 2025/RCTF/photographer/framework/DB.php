<?php
class DB {
    private static $connection = null;
    
    private static function getConnection() {
        if (self::$connection === null) {
            $dbPath = config('db.path');
            
            self::$connection = new SQLite3($dbPath);
            self::$connection->busyTimeout(5000);
            self::$connection->exec('PRAGMA foreign_keys = ON');
        }
        return self::$connection;
    }
    
    public static function table($table) {
        return new QueryBuilder(self::getConnection(), $table);
    }
    
    public static function query($sql) {
        return self::getConnection()->query($sql);
    }
    
    public static function exec($sql) {
        return self::getConnection()->exec($sql);
    }
    
    public static function prepare($sql) {
        return self::getConnection()->prepare($sql);
    }
    
    public static function escape($string) {
        return self::getConnection()->escapeString($string);
    }
    
    public static function lastInsertId() {
        return self::getConnection()->lastInsertRowID();
    }
    
    public static function beginTransaction() {
        return self::exec('BEGIN TRANSACTION');
    }
    
    public static function commit() {
        return self::exec('COMMIT');
    }
    
    public static function rollback() {
        return self::exec('ROLLBACK');
    }
}

class QueryBuilder {
    private $db;
    private $table;
    private $select = ['*'];
    private $joins = [];
    private $wheres = [];
    private $orderBy = [];
    private $limit = null;
    private $offset = null;
    private $groupBy = [];
    private $bindings = [];
    private $bindingCounter = 0;
    
    public function __construct($db, $table) {
        $this->db = $db;
        $this->table = $table;
    }
    
    public function select($columns = ['*']) {
        if (is_string($columns)) {
            $columns = [$columns];
        }
        $this->select = $columns;
        return $this;
    }
    
    public function join($table, $first, $operator, $second) {
        $this->joins[] = [
            'type' => 'INNER',
            'table' => $table,
            'first' => $first,
            'operator' => $operator,
            'second' => $second
        ];
        return $this;
    }
    
    public function leftJoin($table, $first, $operator, $second) {
        $this->joins[] = [
            'type' => 'LEFT',
            'table' => $table,
            'first' => $first,
            'operator' => $operator,
            'second' => $second
        ];
        return $this;
    }
    
    public function rightJoin($table, $first, $operator, $second) {
        $this->joins[] = [
            'type' => 'RIGHT',
            'table' => $table,
            'first' => $first,
            'operator' => $operator,
            'second' => $second
        ];
        return $this;
    }
    
    public function where($column, $operator = '=', $value = null, $boolean = 'AND') {
        if ($value === null) {
            $value = $operator;
            $operator = '=';
        }
        
        $bindKey = ':bind_' . $this->bindingCounter++;
        $this->wheres[] = [
            'column' => $column,
            'operator' => $operator,
            'value' => $bindKey,
            'boolean' => $boolean
        ];
        $this->bindings[$bindKey] = $value;
        return $this;
    }
    
    public function orWhere($column, $operator = '=', $value = null) {
        return $this->where($column, $operator, $value, 'OR');
    }
    
    public function whereIn($column, array $values, $boolean = 'AND') {
        $bindKeys = [];
        foreach ($values as $value) {
            $bindKey = ':bind_' . $this->bindingCounter++;
            $bindKeys[] = $bindKey;
            $this->bindings[$bindKey] = $value;
        }
        
        $this->wheres[] = [
            'type' => 'IN',
            'column' => $column,
            'values' => $bindKeys,
            'boolean' => $boolean
        ];
        return $this;
    }
    
    public function whereNull($column, $boolean = 'AND') {
        $this->wheres[] = [
            'type' => 'NULL',
            'column' => $column,
            'boolean' => $boolean
        ];
        return $this;
    }
    
    public function whereNotNull($column, $boolean = 'AND') {
        $this->wheres[] = [
            'type' => 'NOT_NULL',
            'column' => $column,
            'boolean' => $boolean
        ];
        return $this;
    }
    
    public function orderBy($column, $direction = 'ASC') {
        $this->orderBy[] = [
            'column' => $column,
            'direction' => strtoupper($direction)
        ];
        return $this;
    }
    
    public function groupBy($columns) {
        if (is_string($columns)) {
            $columns = [$columns];
        }
        $this->groupBy = array_merge($this->groupBy, $columns);
        return $this;
    }
    
    public function limit($limit) {
        $this->limit = $limit;
        return $this;
    }
    
    public function offset($offset) {
        $this->offset = $offset;
        return $this;
    }
    
    private function buildSql() {
        $selectFields = $this->select === ['*'] ? '*' : implode(', ', $this->select);
        $sql = "SELECT {$selectFields} FROM {$this->table}";
        
        foreach ($this->joins as $join) {
            $sql .= " {$join['type']} JOIN {$join['table']}";
            $sql .= " ON {$join['first']} {$join['operator']} {$join['second']}";
        }
        
        if (!empty($this->wheres)) {
            $sql .= " WHERE";
            $first = true;
            foreach ($this->wheres as $where) {
                if (!$first) {
                    $sql .= " {$where['boolean']}";
                }
                
                if (isset($where['type'])) {
                    switch ($where['type']) {
                        case 'IN':
                            $sql .= " {$where['column']} IN (" . implode(', ', $where['values']) . ")";
                            break;
                        case 'NULL':
                            $sql .= " {$where['column']} IS NULL";
                            break;
                        case 'NOT_NULL':
                            $sql .= " {$where['column']} IS NOT NULL";
                            break;
                    }
                } else {
                    $sql .= " {$where['column']} {$where['operator']} {$where['value']}";
                }
                
                $first = false;
            }
        }
        
        if (!empty($this->groupBy)) {
            $sql .= " GROUP BY " . implode(', ', $this->groupBy);
        }
        
        if (!empty($this->orderBy)) {
            $sql .= " ORDER BY";
            $first = true;
            foreach ($this->orderBy as $order) {
                if (!$first) $sql .= ",";
                $sql .= " {$order['column']} {$order['direction']}";
                $first = false;
            }
        }
        
        if ($this->limit !== null) {
            $sql .= " LIMIT {$this->limit}";
        }
        
        if ($this->offset !== null) {
            $sql .= " OFFSET {$this->offset}";
        }
        
        return $sql;
    }
    
    public function get() {
        $sql = $this->buildSql();
        $stmt = $this->db->prepare($sql);
        
        foreach ($this->bindings as $key => $value) {
            $type = is_int($value) ? SQLITE3_INTEGER : SQLITE3_TEXT;
            $stmt->bindValue($key, $value, $type);
        }
        
        $result = $stmt->execute();
        $rows = [];
        
        while ($row = $result->fetchArray(SQLITE3_ASSOC)) {
            $rows[] = $row;
        }
        
        return $rows;
    }
    
    public function first() {
        $this->limit(1);
        $results = $this->get();
        return !empty($results) ? $results[0] : null;
    }
    
    public function count() {
        $sql = "SELECT COUNT(*) as count FROM {$this->table}";
        
        foreach ($this->joins as $join) {
            $sql .= " {$join['type']} JOIN {$join['table']}";
            $sql .= " ON {$join['first']} {$join['operator']} {$join['second']}";
        }
        
        if (!empty($this->wheres)) {
            $sql .= " WHERE";
            $first = true;
            foreach ($this->wheres as $where) {
                if (!$first) {
                    $sql .= " {$where['boolean']}";
                }
                
                if (isset($where['type'])) {
                    switch ($where['type']) {
                        case 'IN':
                            $sql .= " {$where['column']} IN (" . implode(', ', $where['values']) . ")";
                            break;
                        case 'NULL':
                            $sql .= " {$where['column']} IS NULL";
                            break;
                        case 'NOT_NULL':
                            $sql .= " {$where['column']} IS NOT NULL";
                            break;
                    }
                } else {
                    $sql .= " {$where['column']} {$where['operator']} {$where['value']}";
                }
                
                $first = false;
            }
        }
        
        $stmt = $this->db->prepare($sql);
        
        foreach ($this->bindings as $key => $value) {
            $type = is_int($value) ? SQLITE3_INTEGER : SQLITE3_TEXT;
            $stmt->bindValue($key, $value, $type);
        }
        
        $result = $stmt->execute();
        $row = $result->fetchArray(SQLITE3_ASSOC);
        
        return (int)$row['count'];
    }
    
    public function insert(array $data) {
        $columns = array_keys($data);
        $placeholders = array_map(function($col) { return ':' . $col; }, $columns);
        
        $sql = "INSERT INTO {$this->table} (" . implode(', ', $columns) . ")";
        $sql .= " VALUES (" . implode(', ', $placeholders) . ")";
        
        $stmt = $this->db->prepare($sql);
        
        foreach ($data as $key => $value) {
            $type = is_int($value) ? SQLITE3_INTEGER : SQLITE3_TEXT;
            $stmt->bindValue(':' . $key, $value, $type);
        }
        
        $result = $stmt->execute();
        
        if ($result) {
            return $this->db->lastInsertRowID();
        }
        
        return false;
    }
    
    public function update(array $data) {
        $sets = [];
        foreach (array_keys($data) as $key) {
            $sets[] = "{$key} = :{$key}";
        }
        
        $sql = "UPDATE {$this->table} SET " . implode(', ', $sets);
        
        if (!empty($this->wheres)) {
            $sql .= " WHERE";
            $first = true;
            foreach ($this->wheres as $where) {
                if (!$first) {
                    $sql .= " {$where['boolean']}";
                }
                
                if (isset($where['type'])) {
                    switch ($where['type']) {
                        case 'IN':
                            $sql .= " {$where['column']} IN (" . implode(', ', $where['values']) . ")";
                            break;
                        case 'NULL':
                            $sql .= " {$where['column']} IS NULL";
                            break;
                        case 'NOT_NULL':
                            $sql .= " {$where['column']} IS NOT NULL";
                            break;
                    }
                } else {
                    $sql .= " {$where['column']} {$where['operator']} {$where['value']}";
                }
                
                $first = false;
            }
        }
        
        $stmt = $this->db->prepare($sql);
        
        foreach ($data as $key => $value) {
            $type = is_int($value) ? SQLITE3_INTEGER : SQLITE3_TEXT;
            $stmt->bindValue(':' . $key, $value, $type);
        }
        
        foreach ($this->bindings as $key => $value) {
            $type = is_int($value) ? SQLITE3_INTEGER : SQLITE3_TEXT;
            $stmt->bindValue($key, $value, $type);
        }
        
        return $stmt->execute() !== false;
    }
    
    public function delete() {
        $sql = "DELETE FROM {$this->table}";
        
        if (!empty($this->wheres)) {
            $sql .= " WHERE";
            $first = true;
            foreach ($this->wheres as $where) {
                if (!$first) {
                    $sql .= " {$where['boolean']}";
                }
                
                if (isset($where['type'])) {
                    switch ($where['type']) {
                        case 'IN':
                            $sql .= " {$where['column']} IN (" . implode(', ', $where['values']) . ")";
                            break;
                        case 'NULL':
                            $sql .= " {$where['column']} IS NULL";
                            break;
                        case 'NOT_NULL':
                            $sql .= " {$where['column']} IS NOT NULL";
                            break;
                    }
                } else {
                    $sql .= " {$where['column']} {$where['operator']} {$where['value']}";
                }
                
                $first = false;
            }
        }
        
        $stmt = $this->db->prepare($sql);
        
        foreach ($this->bindings as $key => $value) {
            $type = is_int($value) ? SQLITE3_INTEGER : SQLITE3_TEXT;
            $stmt->bindValue($key, $value, $type);
        }
        
        return $stmt->execute() !== false;
    }
    
    public function toSql() {
        return $this->buildSql();
    }
    
    public function getBindings() {
        return $this->bindings;
    }
}
