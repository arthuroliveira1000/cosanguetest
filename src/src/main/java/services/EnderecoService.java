package services;

import java.util.List;

import managers.SimpleEntityManager;
import daos.EnderecoDAO;
import entities.Endereco;

public class EnderecoService {

	private EnderecoDAO dao;
	private SimpleEntityManager simpleEntityManager;

	public EnderecoService(SimpleEntityManager simpleEntityManager) {
		super();
		this.simpleEntityManager = simpleEntityManager;
		this.dao = new EnderecoDAO(simpleEntityManager.getEntityManager());
	}

	public void save(Endereco endereco) {
		try {
			simpleEntityManager.beginTransaction();
			// FAZER VALIDA��O DE CAMPOS NULOS, ETC
			dao.save(endereco);
			simpleEntityManager.commit();
		} catch (Exception e) {
			e.printStackTrace();
			simpleEntityManager.rollBack();
		}
	}

	public List<Endereco> findAll() {
		return dao.findAll(); // VERIFICAR SE N�O RETORNA ERRO CASO LISTA FOR
								// VAZIA
	}

}
