# miniTomcat
使用原生的方式实现了web容器。监听端口，接受请求，分装参数，调用相应的controller。
具体步骤：
1. 使用socket监听端口。
2. 接收到http请求数据，解析数据，得到相应的请求方法、请求参数、浏览器信息等。
3. controller使用注解标记controller、方法路由。
4. 扫描controller包，得到所有的路由信息。
5. 根据请求信息解析的路由地址来匹配所有的controller的路由信息，匹配则执行相关的方法，否则返回404。
