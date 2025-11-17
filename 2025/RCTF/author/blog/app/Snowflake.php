<?php
/**
 * Snowflake ID Generator
 * Generates unique 64-bit IDs similar to Twitter's Snowflake
 */
class Snowflake {

    const EPOCH = 1761926400000;
    
    // Bit lengths
    const TIMESTAMP_BITS = 41;
    const DATACENTER_BITS = 5;
    const WORKER_BITS = 5;
    const SEQUENCE_BITS = 12;
    
    // Max values
    const MAX_DATACENTER_ID = 31;  // 2^5 - 1
    const MAX_WORKER_ID = 31;      // 2^5 - 1
    const MAX_SEQUENCE = 4095;     // 2^12 - 1
    
    private $datacenterId;
    private $workerId;
    private $sequence = 0;
    private $lastTimestamp = -1;
    
    public function __construct($datacenterId = 0, $workerId = 0) {
        if ($datacenterId > self::MAX_DATACENTER_ID || $datacenterId < 0) {
            throw new Exception("Datacenter ID must be between 0 and " . self::MAX_DATACENTER_ID);
        }
        if ($workerId > self::MAX_WORKER_ID || $workerId < 0) {
            throw new Exception("Worker ID must be between 0 and " . self::MAX_WORKER_ID);
        }
        
        $this->datacenterId = $datacenterId;
        $this->workerId = $workerId;
    }
    
    public function nextId() {
        $timestamp = $this->currentTimestamp();
        
        // Clock moved backwards
        if ($timestamp < $this->lastTimestamp) {
            throw new Exception("Clock moved backwards. Refusing to generate ID");
        }
        
        // Same millisecond
        if ($timestamp == $this->lastTimestamp) {
            $this->sequence = ($this->sequence + 1) & self::MAX_SEQUENCE;
            if ($this->sequence == 0) {
                // Sequence overflow, wait for next millisecond
                $timestamp = $this->waitNextMillis($this->lastTimestamp);
            }
        } else {
            $this->sequence = 0;
        }
        
        $this->lastTimestamp = $timestamp;
        
        // Generate ID
        $id = (($timestamp - self::EPOCH) << (self::DATACENTER_BITS + self::WORKER_BITS + self::SEQUENCE_BITS))
            | ($this->datacenterId << (self::WORKER_BITS + self::SEQUENCE_BITS))
            | ($this->workerId << self::SEQUENCE_BITS)
            | $this->sequence;
        
        return $id;
    }
    
    private function currentTimestamp() {
        return floor(microtime(true) * 1000);
    }
    
    private function waitNextMillis($lastTimestamp) {
        $timestamp = $this->currentTimestamp();
        while ($timestamp <= $lastTimestamp) {
            $timestamp = $this->currentTimestamp();
        }
        return $timestamp;
    }
    
    // Parse snowflake ID
    public static function parse($id) {
        $timestamp = ($id >> (self::DATACENTER_BITS + self::WORKER_BITS + self::SEQUENCE_BITS)) + self::EPOCH;
        $datacenterId = ($id >> (self::WORKER_BITS + self::SEQUENCE_BITS)) & self::MAX_DATACENTER_ID;
        $workerId = ($id >> self::SEQUENCE_BITS) & self::MAX_WORKER_ID;
        $sequence = $id & self::MAX_SEQUENCE;
        
        return [
            'timestamp' => $timestamp,
            'datacenter_id' => $datacenterId,
            'worker_id' => $workerId,
            'sequence' => $sequence,
            'datetime' => date('Y-m-d H:i:s', $timestamp / 1000)
        ];
    }
}

