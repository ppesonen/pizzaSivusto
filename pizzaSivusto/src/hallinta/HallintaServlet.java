package hallinta;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Pizza;
import bean.Tayte;
import daot.HallintaDao;

/**
 * Servlet implementation class HallintaServlet
 */
@WebServlet(name = "hallinta", urlPatterns = { "/hallinta" })
public class HallintaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	// Määritetään sivuston path linkkejä ja redirectejä varten
	private String sivustopath = "/reptilemafia";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HallintaServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// Sessionhallintaa
		HttpSession sessio = request.getSession(true);
		
		// Asetetaan sivun path
		request.setAttribute("pathi", sivustopath);

		// Daon alustus
		HallintaDao dao = new HallintaDao();
		
		// Pizzojen ja täytteiden haku
		ArrayList<Pizza> pizzat = dao.haeKaikkiPizzat();
		ArrayList<Tayte> taytteet = dao.haeKaikkiTaytteet();
		
		request.setAttribute("pizzat", pizzat);
		request.setAttribute("taytteet", taytteet);
		
		RequestDispatcher rd = request.getRequestDispatcher("hallinta.jsp");
		rd.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}