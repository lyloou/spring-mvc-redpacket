local listKey = 'red_packet_list_'..KEYS[1]
local redPacket = 'red_packet_'..KEYS[1]
local stock = tonumber(redis.call('hget', redPacket, 'stock'))
if stock <= 0 then return 0 end
stock = stock - 1
redis.call('hset', redPacket, 'stock', tostring(stock))
redis.call('rpush', listKey, ARGV[1])
if stock == 0 then return 2 end
return 1