-- KEYS[1] = 库存 key
-- KEYS[2] = 用户已购 Set key
-- ARGV[1] = 用户 ID
-- 返回：1 成功，0 库存不足，-1 重复下单

local stock = tonumber(redis.call('get', KEYS[1]))
if stock == nil or stock <= 0 then
    return 0
end

-- 判断是否已抢过
if redis.call('sismember', KEYS[2], ARGV[1]) == 1 then
    return -1
end

-- 扣库存 + 记录用户
redis.call('decr', KEYS[1])
redis.call('sadd', KEYS[2], ARGV[1])
return 1