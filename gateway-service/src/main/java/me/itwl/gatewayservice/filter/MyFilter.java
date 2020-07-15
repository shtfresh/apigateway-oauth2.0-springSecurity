package me.itwl.gatewayservice.filter;




import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Base64;
import java.util.List;

@Component
public class MyFilter implements GlobalFilter, Ordered {

    final Base64.Decoder decoder = Base64.getDecoder();
    final Base64.Encoder encoder = Base64.getEncoder();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (!(authentication instanceof OAuth2Authentication)) {
//            System.out.println("+++++++++++++++++++++++++++++++++");
//            // return null;
//        } else {
//            System.out.println("--------------------------------");
//        }
        URI uri =exchange.getRequest().getURI();
        String url=uri.getPath();
        //遇到不需要传递token的直接放行
        if(url.equals("/test/test")){
            return chain.filter(exchange);
        }
        String authorization = exchange.getRequest().getHeaders().getFirst("Authorization");
        if(authorization == null) {
            return Mono.empty();
        }

        String[] strArr = authorization.split(" ");
        String[] strArr2 = strArr[1].split("\\.");
        String  var = strArr2[1];



        JSONObject jsonObect =  JSON.parseObject(new String(decoder.decode(var)));


        exchange.getRequest().mutate().headers(httpHeaders -> {
            httpHeaders.add("user_name",jsonObect.getString("user_name"));
            httpHeaders.add("authorities",jsonObect.getString("authorities"));
            httpHeaders.add("client_id",jsonObect.getString("client_id"));
            httpHeaders.add("scope",jsonObect.getString("scope"));
        }).build();

//      List<String> listAuthorization =  exchange.getRequest().getHeaders().get("Authorization");
//        String jwt = listAuthorization.get(0);
//        System.out.println(jwt);

                return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
