$(function() {
	var decimal=$('.js-decimal'); //aqui chamo a classe que esta la no cerveja cadastro
	decimal.maskMoney();
		  
	var plain= $('.js-plain');
	plain.maskMoney({precision:0});//neste caso digo que não haverá casa decimal, para estoque
});