<?php
class Snowflake {
    private $epoch = 1609459200000;
    private $workerIdBits = 5;
    private $datacenterIdBits = 5;
    private $sequenceBits = 12;
    
    private $workerId;
    private $datacenterId;
    private $sequence = 0;
    private $lastTimestamp = -1;
    
    public function __construct($workerId = 1, $datacenterId = 1) {
        $maxWorkerId = -1 ^ (-1 << $this->workerIdBits);
        $maxDatacenterId = -1 ^ (-1 << $this->datacenterIdBits);
        
        if ($workerId > $maxWorkerId || $workerId < 0) {
            throw new Exception("Worker ID out of range");
        }
        
        if ($datacenterId > $maxDatacenterId || $datacenterId < 0) {
            throw new Exception("Datacenter ID out of range");
        }
        
        $this->workerId = $workerId;
        $this->datacenterId = $datacenterId;
    }
    
    public function nextId() {
        $timestamp = $this->timeGen();
        
        if ($timestamp < $this->lastTimestamp) {
            throw new Exception("Clock moved backwards");
        }
        
        if ($timestamp == $this->lastTimestamp) {
            $sequenceMask = -1 ^ (-1 << $this->sequenceBits);
            $this->sequence = ($this->sequence + 1) & $sequenceMask;
            
            if ($this->sequence == 0) {
                $timestamp = $this->tilNextMillis($this->lastTimestamp);
            }
        } else {
            $this->sequence = 0;
        }
        
        $this->lastTimestamp = $timestamp;
        
        $timestampLeftShift = $this->sequenceBits + $this->workerIdBits + $this->datacenterIdBits;
        $datacenterIdShift = $this->sequenceBits + $this->workerIdBits;
        $workerIdShift = $this->sequenceBits;
        
        return (($timestamp - $this->epoch) << $timestampLeftShift)
            | ($this->datacenterId << $datacenterIdShift)
            | ($this->workerId << $workerIdShift)
            | $this->sequence;
    }
    
    private function tilNextMillis($lastTimestamp) {
        $timestamp = $this->timeGen();
        while ($timestamp <= $lastTimestamp) {
            $timestamp = $this->timeGen();
        }
        return $timestamp;
    }
    
    private function timeGen() {
        return floor(microtime(true) * 1000);
    }
}
