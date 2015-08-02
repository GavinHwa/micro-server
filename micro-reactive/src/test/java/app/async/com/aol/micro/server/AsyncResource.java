package app.async.com.aol.micro.server;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

import org.springframework.stereotype.Component;

import com.aol.micro.server.auto.discovery.RestResource;
import com.aol.micro.server.reactive.Reactive;
import com.aol.micro.server.testing.RestAgent;
import com.google.common.collect.ImmutableList;

@Path("/async")
@Component
public class AsyncResource implements RestResource,Reactive{

	
	private final ImmutableList<String> urls = ImmutableList.of("http://localhost:8080/async-app/async/ping2",
			"http://localhost:8080/async-app/async/ping",
			"http://localhost:8080/async-app/async/ping",
			"http://localhost:8080/async-app/async/ping");
    
    	private final RestAgent client = new RestAgent();
    	
        @GET
        @Path("/expensive")
        @Produces("text/plain")
        public void expensive(@Suspended AsyncResponse asyncResponse){
  
        	this.async(lr -> lr.fromStream(urls.stream()
					.<CompletableFuture<String>>map(it ->  CompletableFuture.completedFuture(client.get(it))))
					.onFail(it -> "")
					.peek(it -> 
					System.out.println(it))
					.<String,Boolean>allOf(data -> {
						System.out.println(data);
							return asyncResponse.resume(String.join(";", (List<String>)data)); })).run();
        	
        }
        
        @GET
    	@Produces("text/plain")
    	@Path("/ping")
    	public String ping() {
    		return "test!";
    	}
    	
	
}