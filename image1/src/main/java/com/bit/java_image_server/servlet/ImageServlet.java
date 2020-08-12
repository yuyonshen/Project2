package com.bit.java_image_server.servlet;

import com.bit.java_image_server.dao.Image;
import com.bit.java_image_server.dao.ImageDao;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;


@WebServlet("/image")
public class ImageServlet extends HttpServlet {
    public static final String PATH_BASE = "E:\\八月\\image1\\photo\\";
//    public static final String PATH_BASE = "\\Users\\Me\\zhaohuiwen\\javaProject\\photo\\";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 这个一定要放在上面
        resp.setContentType("application/json;charset=UTF-8");

        String imageId = req.getParameter("image_id");
        if (imageId == null || imageId.equals("")) {
            // 获取所有图片信息
            doSelectAll(resp);
        } else {
            // 获取指定图片信息
            doSelectOne(Integer.valueOf(imageId), resp);
        }
    }

    private void doSelectAll(HttpServletResponse resp) throws IOException {
        // 1. 创建 ImageDao 对象并从数据库查找数据
        ImageDao imageDao = new ImageDao();
        List<Image> images = imageDao.selectAll();
        // 2. 将查找到的数据转换成 JSON 格式的字符串
        List<HashMap<String, Object>> data = new ArrayList<>();
        for (Image image : images) {
            // a) 先把每个 image 对象变成一个 HashMap
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("image_id", image.getImageId());
            hashMap.put("image_name", image.getImageName());
            hashMap.put("content_type", image.getContentType());
            hashMap.put("md5", image.getMd5());
            // b) 再把这个 HashMap 填入到 List 中
            data.add(hashMap);
        }
        // c) 最后借助 gson 完成转换就行了. 很简单
        Gson gson = new GsonBuilder().create();
            String respData = gson.toJson(data);
        System.out.println("selectAll:" + respData);
        // 3. 结果字符串写入到 resp 对象中
        resp.getWriter().write(respData);
    }

    private void doSelectOne(int imageId, HttpServletResponse resp) throws IOException {
        // 1. 先创建 ImageDao 对象, 并查找数据库
        ImageDao imageDao = new ImageDao();
        Image image = imageDao.selectOne(imageId);
        // 2. 将 image 对象转成 Json 格式
        HashMap<String, Object> data = new HashMap<>();
        String respData = "";
        if (image != null) {
            data.put("image_id", image.getImageId());
            data.put("image_name", image.getImageName());
            data.put("content_type", image.getContentType());
            data.put("md5", image.getMd5());
        } else {
            // 如果数据没找到
            data.put("ok", false);
            data.put("message", "未找到指定图片");
        }
        Gson gson = new GsonBuilder().create();
        respData = gson.toJson(data);
        // 3. 将 Json 字符串写入 resp 对象中
        resp.getWriter().write(respData);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 上传图片
        resp.setContentType("application/json; charset=utf-8");
        // 1. 获取到图片相关的元信息(Image对象), 并写入数据库
        //  a) 创建 factory 对象和 upload 对象
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        List<FileItem> items = null;
        //  b) 使用 upload 对象解析请求
        try {
            items = upload.parseRequest(req);
        } catch (FileUploadException e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().println("{ \"ok\" : false, \"reason\" : \"请求解析失败\"}");
            return;
        }
        //  c) 对请求信息进行解析, 转换成 Image 对象
        //     一个请求可以上传一个文件, 也能上传多个文件
        //     当前只考虑上传一个的情况
        FileItem item = items.get(0);
        Image image = new Image();
        image.setImageName(item.getName());
        image.setSize((int)item.getSize());
        SimpleDateFormat df = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        image.setUploadTime(df.format(new Date()));
        image.setMd5(DigestUtils.md5Hex(item.get()));
        image.setContentType(item.getContentType());
        image.setPath(PATH_BASE + image.getMd5());
        //  d) 将 Image 对象写入数据库中
        ImageDao imageDao = new ImageDao();
        // 根据 md5 查询是否已经有 Image 存在
        Image existImage = imageDao.selectByMD5(image.getMd5());
        imageDao.insert(image);

        // 2. 获取到图片内容, 写入到磁盘中. 如果图片存在就不写文件了
        if (existImage == null) {
            File file = new File(image.getPath());
            try {
                item.write(file);
            } catch (Exception e) {
                e.printStackTrace();
                resp.setStatus(500);
                // 注意, 此处可能出现脏数据的情况, 比如数据库插入成功, 但是这里文件写入失败
                resp.getWriter().println("{ \"ok\" : false, \"reason\" : \"文件写入失败\"}");
                return;
            }
        }
        // 3. 设置返回的响应结果
        // 先不使用 GSON 来构建 Json 字符串
        resp.sendRedirect("index.html");
        resp.getWriter().println("{ \"ok\": true }");
    }
    //删除该页面
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HashMap<String, Object> data = new HashMap<>();
        Gson gson = new GsonBuilder().create();

        // 1. 先获取到请求中的 image_id
        String imageId = req.getParameter("image_id");
        if (imageId == null || imageId.equals("")) {
            data.put("ok", false);
            data.put("reason", "请求格式错误");
            String respData = gson.toJson(data);
            resp.getWriter().write(respData);
            return;
        }
        // 2. 创建 ImageDao 对象, 和对应的 image 对象
        ImageDao imageDao = new ImageDao();
        Image image = imageDao.selectOne(Integer.valueOf(imageId));
        //  a) 删除数据库
        boolean ret = imageDao.delete(Integer.valueOf(imageId));
        if (!ret) {
            data.put("ok", false);
            data.put("reason", "图片不存在");
            String respData = gson.toJson(data);
            resp.getWriter().write(respData);
            return;
        }
        //  b) 删除磁盘文件, 此时说明已经不存在相同 MD5 的图片了, 可以删除磁盘上的图片文件
        Image existImage = imageDao.selectByMD5(image.getMd5());
        if (existImage == null) {
            File file = new File(image.getPath());
            file.delete();
        }
        // 3. 构造最终响应结果
        data.put("ok", true);
        String respData = gson.toJson(data);
        resp.getWriter().write(respData);
    }
}
