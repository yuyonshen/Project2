package filter;

import model.Response;
import util.JSONUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

//过滤器,满足http请求的url匹配过滤器路径的规则,才会过滤
@WebFilter("/*")
public class LoginFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse res = (HttpServletResponse)response;
        //静态资源,后台服务
        //需要处理敏感资源(首页,没有登录重定向在我们的登录页面
        //后台的服务资源:除登录接口/user/login以外,其他接口.没有登录返回没有登录的json信息
        HttpSession session = req.getSession(false);

        if(session==null){
            String uri = req.getServletPath();
            if("/public/page/main.html".equals(uri)){//重定向首页
                String schema = req.getScheme();//http
                String host = req.getServerName();//服务域名ip
                int port = req.getServerPort();//服务端口号
                String contextPath = req.getContextPath();//项目部署名
                String basePath = schema+"://"+host+":"+port+contextPath;
                res.sendRedirect(basePath+"/public/index.html");
                return;
            }else if(!"/user/login".equals(uri)&& !uri.startsWith("/public/")
                    && !uri.startsWith("/static/")){
                req.setCharacterEncoding("UTF-8");//请求体编码
                res.setCharacterEncoding("UTF-8");//响应体设置编码
                res.setContentType("application/json; charset=UTF-8");//浏览器接收数据解析方式
                Response r = new Response();
                r.setCode("301");
                r.setMessage("未授权的http请求");
                PrintWriter pw = res.getWriter();
                pw.println(JSONUtil.write(r));
                pw.flush();
                return;
            }
        }
        chain.doFilter(request,response);//过滤器向下调用,再次过滤
    }

    @Override
    public void destroy() {

    }
}
