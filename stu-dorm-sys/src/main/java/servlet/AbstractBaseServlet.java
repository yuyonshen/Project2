package servlet;

import model.Response;
import util.JSONUtil;
import util.ThreadLocalHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;


public abstract class AbstractBaseServlet extends HttpServlet {

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            doPost(req, resp);
        }

        //设计模式：模板模式
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            req.setCharacterEncoding("UTF-8");//请求体编码
            resp.setCharacterEncoding("UTF-8");//响应体设置编码
            resp.setContentType("application/json; charset=UTF-8");//浏览器接收数据解析方式

            Response response = new Response();
            try {
                response.setData(process(req, resp));
                response.setSuccess(true);
                response.setCode("200");
                response.setMessage("操作成功");
                response.setTotal(ThreadLocalHolder.get().get());
            } catch (Exception e) {//出现异常，返回success=false，并设置错误消息，异常堆栈
                response.setCode("500");

                response.setMessage(e.getMessage());
                StringWriter sw = new StringWriter();
                PrintWriter writer = new PrintWriter(sw);
                e.printStackTrace(writer);
                response.setStackTrace(sw.toString());
            }finally {
                ThreadLocalHolder.get().remove();
            }
            //响应数据，json数据
            PrintWriter pw = resp.getWriter();
            pw.println(JSONUtil.write(response));
            pw.flush();
        }

    public abstract Object process(HttpServletRequest req, HttpServletResponse resp) throws Exception;
}
