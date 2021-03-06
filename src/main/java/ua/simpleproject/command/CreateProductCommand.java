package ua.simpleproject.command;

import org.apache.log4j.Logger;
import ua.simpleproject.entity.ProductCurrentCheque;
import ua.simpleproject.entity.Product;
import ua.simpleproject.services.CreateProductLogic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CreateProductCommand implements ActionCommand{
    private CreateProductLogic createProductLogic = new CreateProductLogic();
    private Logger logger = Logger.getLogger(CreateProductCommand.class);

    @Override
    public String execute(HttpServletRequest request) {
        List<ProductCurrentCheque> productCurrentCheckList = new ArrayList<>();
        String page = null;
//  взяти назву s код товару і їх кількість з запиту
        String name = (String) request.getParameter("name");
        String codeStr = (String) request.getParameter("code");
        int code = Integer.parseInt(codeStr);
        String countableStr = (String) request.getParameter("countable");
        boolean countable = Boolean.parseBoolean(countableStr);
        String priceForOneStr = (String) request.getParameter("price");
        BigDecimal priceForOne = new BigDecimal(priceForOneStr);
        Product product = new Product(code, name,countable,priceForOne);
        HttpSession session = request.getSession();
        String login = (String) session.getAttribute("login");
        if (Objects.isNull(login)){
            page = "/login.jsp";
            logger.error("");
            return page;
        }
        try{
            createProductLogic.createProductStock(product);
            page = "/WEB-INF/jsp/general.jsp";
            return page;
        }catch(Exception e){
            page = "/WEB-INF/jsp//error/error1.jsp";
            logger.error("", e);
            return page;
        }
    }
}
