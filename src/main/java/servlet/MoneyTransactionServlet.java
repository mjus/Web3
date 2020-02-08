package servlet;

import dao.BankClientDAO;
import model.BankClient;
import model.BankClient;
import service.BankClientService;
import util.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MoneyTransactionServlet extends HttpServlet {

    BankClientService bankClientService = new BankClientService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().println(PageGenerator.getInstance().getPage("moneyTransactionPage.html", new HashMap<>()));
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String senderName = req.getParameter("senderName");
        String senderPass = req.getParameter("senderPass");
        String count = req.getParameter("count");
        String nameTo = req.getParameter("nameTo");
        Map<String, Object> pageVariables = createPageVariablesMap(req);

        BankClient bc = bankClientService.getClientByName(senderName);
        if (bc != null && isNumber(count) && bc.getPassword().equals(senderPass)
                && bankClientService.sendMoneyToClient(bc, nameTo, Long.parseLong(count))) {
            String message = "The transaction was successful";
            pageVariables.put("message", message);
            resp.getWriter().println(PageGenerator.getInstance().getPage("resultPage.html", pageVariables));
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            String message = "transaction rejected";
            pageVariables.put("message", message);
            resp.getWriter().println(PageGenerator.getInstance().getPage("resultPage.html", pageVariables));
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }

    private static Map<String, Object> createPageVariablesMap(HttpServletRequest request) {
        Map<String, Object> pageVariables = new HashMap<>();
        return pageVariables;
    }

    private boolean isNumber(String s) {
        try {
            Long.parseLong(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}