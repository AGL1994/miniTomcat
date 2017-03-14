package controller;

import org.agl.webContent.annotation.Param;
import org.agl.webContent.annotation.RequestUrl;
import org.agl.webContent.annotation.ResponseType;
import org.agl.webContent.entity.Request;
import org.agl.webContent.entity.Response;

@RequestUrl("say")
public class TestController {
	@RequestUrl("sayHello")
	public String doGet(@Param("name") String name, @Param("age") int age){
		System.out.println("coming in doGet1");
		return "这是方法的返回值";
	}
	
	@RequestUrl("sayHello1")
	public String doGet1(String name, int age){
		System.out.println("coming in doGet2");
		return "这是方法的返回值";
	}
	
	@RequestUrl("sayHello2")
	public String doGet2(@Param("name") String name,Request req ,int age,Response response){
		System.out.println("coming in doGet3");
		System.out.println(req.getActionMethod());
		System.out.println(response);
		return "这是方法的返回值";
	}
}
