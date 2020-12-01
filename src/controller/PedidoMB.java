package controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import dao.ClienteDAO;
import dao.PedidoDAO;
import entidades.Cliente;
import entidades.Pedido;
import model.PedidoVO;
import utilitarios.GeradorID;

@Named 
@SessionScoped
public class PedidoMB implements Serializable{
	private static final long serialVersionUID = 1L;
	private PedidoVO pedido = new PedidoVO();	
	private List<PedidoVO> pedidos;
	private Integer idCliente;
	private ClienteDAO clienteDAO = new ClienteDAO();
	private Cliente cliente;
	static PedidoDAO dao = new PedidoDAO();
	
	public PedidoMB() {
		
	}
 	
	public String salvar() {	 
		 pedido.setIdCliente(cliente);
        // se o "id" do objeto "pedidoVO" está NULL significa um "novo pedido"
		if (this.pedido.getId()==null) {
			cadastrarNovoPedido();
		} else
			atualizarPedido();
		return "";
	}
	
	public String novoPedido() {
		this.pedido = new PedidoVO();
		pedido.setIdCliente(cliente);
		FacesContext.getCurrentInstance().addMessage(null,
	                new FacesMessage(FacesMessage.SEVERITY_INFO, 
	                		"Informe os dados do novo pedido", ""));	       
		return "";	
	}
	
    // método privado para incluir um novo pedido na base dados.
	private void cadastrarNovoPedido() {
		boolean incluiu = dao.incluir(pedido);
		if (incluiu) 
		   FacesContext.getCurrentInstance().addMessage(null, 
				new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Pedido <"+pedido.getNomeProduto() + "> "
					+ " cadastrado com ID="+pedido.getId(), null));

		else
		   FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro na Operação!", null));
		// limpa o "VO" para incluir um novo
		this.pedido = new PedidoVO();	
		pedido.setIdCliente(cliente);
	}

	// método privado para alterar os dados do pedido na base dados.
	private void atualizarPedido() {		
		boolean ok = dao.atualiza(this.pedido);
		if (ok)
		   FacesContext.getCurrentInstance().addMessage(null, 
				new FacesMessage(FacesMessage.SEVERITY_INFO, 
						"Pedido <" + this.pedido.getNomeProduto()
						+ "> atualizado com sucesso!", null));
		else
		   FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, 
						"Erro na Operação!", null));
		// limpa o "VO" para incluir um novo
		this.pedido = new PedidoVO();	
		pedido.setIdCliente(cliente);
	}
	
	public void delete(String id) {
		int idPK = Integer.parseInt(id);	
		Pedido ped = dao.findById(idPK);
		dao.delete(idPK);       
		FacesContext.getCurrentInstance().addMessage(null, 
				new FacesMessage(FacesMessage.SEVERITY_INFO, 
						"Pedido <" + ped.getNomeProduto()
						+ "> Excluído com sucesso!", null));
    }
	
	public String update(String id) {
		int idPK = Integer.parseInt(id);		 
	    Pedido ped = dao.findById(idPK);
	    this.pedido.setId(ped.getId());
	    this.pedido.setNomeProduto(ped.getNomeProduto());
	    this.pedido.setQuantidade(ped.getQuantidade());
	    this.pedido.setValorTotal(ped.getValorTotal());
	    return "";
	}
	
	public String updateID(String id) {
		int idPK = Integer.parseInt(id);		 
	    Pedido ped = dao.findById(idPK);
	    this.pedido.setIdCliente(ped.getIdCliente());
	    return "";
	}
	
	// getters e setters
	public PedidoVO getPedido() {
		return this.pedido;
	}
	public void setPedido(PedidoVO pedido) {
		this.pedido = pedido;
	}

	public List<PedidoVO> getPedidos() {
		List<Pedido> pedidosEnt = dao.getPedidos(cliente.getId());
		this.pedidos = new ArrayList<PedidoVO>();
		for (Pedido pedido : pedidosEnt) {			
			
			PedidoVO vo = new PedidoVO();
			
			vo.setId(pedido.getId());
			vo.setNomeProduto(pedido.getNomeProduto());
			vo.setQuantidade(pedido.getQuantidade());
			vo.setValorTotal(pedido.getValorTotal());
			vo.setIdCliente(pedido.getIdCliente());
			this.pedidos.add(vo);	
		}		
		
		return this.pedidos;
	}

	public void setClientes(List<PedidoVO> pedidos) {
		this.pedidos = pedidos;
	}
	
	public String idCliente(String idClient) {
		cliente = clienteDAO.findById(Integer.parseInt(idClient));
		return "cadastro-pedidos.xhtml";
	}
	
	public Double getValorPedidos() {
		List<Pedido> pedidos = dao.getPedidos(cliente.getId());
		return pedidos.stream().mapToDouble(Pedido::getValorTotal).sum();
	}
}
