package model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.servlet.http.HttpServletRequest;

@Getter
@ToString
@Setter
public class Page {
    private String searchText;
    private String sortOrder;
    private Integer pageSize;
    private Integer pageNumber;
    /*
    request输入流只能获取请求体中的数据,依赖程序自己解析
    req.getParameter可以获取请求体和url中的数据.
     */

    public static Page parse(HttpServletRequest req){
        Page p = new Page();
        p.searchText = req.getParameter("searchText");
        p.sortOrder = req.getParameter("sortOrder");
        p.pageSize = Integer.parseInt(req.getParameter("pageSize"));
        p.pageNumber = Integer.parseInt(req.getParameter("pageNumber"));
        return p;
    }
}
