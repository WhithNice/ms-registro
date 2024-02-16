package com.codigo.msregistro.infraestructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service    // creamos metodos, pero no BEANS, los beans los creamos con el @Application.
@RequiredArgsConstructor
public class RedisService {
    private final StringRedisTemplate stringRedisTemplate;
    public void saveInRedis(String key, String valor, int exp) {
        stringRedisTemplate.opsForValue().set(key, valor);
        stringRedisTemplate.expire(key, exp, TimeUnit.MINUTES);     // este es el tiepo para guardar el tiempo en cache (aca mismo se limita el tiempo de cuanto maximo debe durar un dato, por ejemplo una clave dinamica de un banco)
    }

    public  String getFromRedis(String key) {
        return stringRedisTemplate.opsForValue().get(key);  // me devuelve la clave que quiero recuperar (como por ejemplo uan clave dinamica)
    }

    public void deleteKey(String key) {
        stringRedisTemplate.delete(key);    // este metodo es importante porque por ejemplo, si la clave dinamica de un banco ya ha sido usado, por ende ya no serviria tenerlo guardado hasta que se expire como en el metodo "saveInRedis" se ha establecido.
    }
}
