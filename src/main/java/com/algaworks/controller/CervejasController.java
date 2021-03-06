package com.algaworks.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.aspectj.internal.lang.annotation.ajcPrivileged;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.SystemPropertyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.algaworks.controller.page.PageWrapper;
import com.algaworks.model.Cerveja;
import com.algaworks.model.Origem;
import com.algaworks.model.Sabor;
import com.algaworks.repository.CervejaRepository;
import com.algaworks.repository.EstilosRepository;
import com.algaworks.repository.filter.CervejaFilter;
import com.algaworks.service.CadastroCervejaService;

@Controller

@RequestMapping("/cervejas")

public class CervejasController {
	
	

	@Autowired //aqui vou injetar o repository com a variavel abaixo
	private CervejaRepository cervejaRepository;
	
	@Autowired
	private EstilosRepository estiloRepository;
	
	@Autowired
	private CadastroCervejaService cadastroCervejaService;

	

	
	private static final Logger logger = LoggerFactory.getLogger(CervejasController.class);
	
	@RequestMapping("/novo")
	 @GetMapping
	 public ModelAndView novo(Cerveja cerveja){ 
		ModelAndView mv = new ModelAndView("/cerveja/CadastroCerveja");
		
		mv.addObject("sabores", Sabor.values()); //crio a variavel sabor(QUE É UM ENUM) que tera esse mesmo nome la na view e coloco dentro dessa variavel o array de sabores
		mv.addObject("estilos", estiloRepository.findAll()); //crio a variavel estilos e preeencho ela com todos os estilos buscados do banco atraves do repository
		mv.addObject("origens", Origem.values());//valeus é um array , FindAll é uma lista 
		
		/*
		Optional<Cerveja> cervejaOptional= cervejaRepository.findBySkuIgnoreCase("A111"); //
		System.out.println(cervejaOptional.isPresent()); //existe a cerveja pesquisada? retorna um boolean
		*/
		
		return mv;
	 }
	
	@RequestMapping(value="/novo", method= RequestMethod.POST)
	public ModelAndView cadastrar (@Valid Cerveja cerveja,   //valid adiciona a validação ao campo,,,, adiociono o objeto cerveja aqui ja pra usar o objeto cerveja la na view
			BindingResult result,  //resultado do binding
			Model model,  //para lançar a mensagem de erro 
			RedirectAttributes attributes){ //neste caso consigo adiciionar uma mensagem em uma pagina com redirect. Pois se colocar apenas addAtribute em uma página redirect ela ira desaparecer apos o redirect

			if(result.hasErrors()){ //se ocorreu um erro no preenchimemto da variavel
				return novo(cerveja);
			}
		
			
			cadastroCervejaService.salvar(cerveja);
			
			attributes.addFlashAttribute("mensagem", "Cerveja salva com sucesso"); //agora consigo colocar uma mensagem em uma página redirect
				
			return new ModelAndView("redirect:/cervejas/novo"); //redirect : se deu tudo certo quero que ele va a tela de novo cadastro ! Ou seja faz um novo get para a pagina cadastro
		
	}
	
	@GetMapping   //quando ele digitar cervejas ja cai na pesquisa
	public ModelAndView pesquisar( CervejaFilter cervejaFilter, BindingResult result,@PageableDefault(size=2) Pageable pageable, HttpServletRequest httpServletRequest){ //pageable faz a paginação da tela pesquisa com dois registros por pagina
		//ja crio esse objeto aqui pra usa como objeto la na view
				ModelAndView mv = new ModelAndView("cerveja/PesquisaCerveja");
				
				mv.addObject("estilos", estiloRepository.findAll()); //crio a variavel estilos,pois irei pesquisar por estilo
				mv.addObject("sabores", Sabor.values());
				mv.addObject("origens", Origem.values());
				//mv.addObject("cervejas",cervejaRepository.filtrar(cervejaFilter, pageable));   // a interface CervejaRepository extend a interface CervejaRepositoryQueries
				PageWrapper<Cerveja> paginaWrapper = new PageWrapper<>( cervejaRepository.filtrar(cervejaFilter, pageable),httpServletRequest); //// retorno uma página de cerveja com todos os dados de paginação  pageWrappper contem as funções que trabalham com a paginação
				//com o httpservlet implementado no pagewrapper mantenho o filtro na requisição
				mv.addObject("pagina",paginaWrapper);
				
		
		return mv;
		}

}





