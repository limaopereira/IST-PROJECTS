DROP VIEW IF EXISTS vendas;

CREATE VIEW vendas(ean,cat,ano,trimestre,mes,dia_mes,dia_semana,distrito,concelho,unidades)
AS
	SELECT er.ean,p.cat,
		EXTRACT(YEAR FROM er.instante),
		EXTRACT(QUARTER FROM er.instante),
	       	EXTRACT(MONTH FROM er.instante),
		EXTRACT(DAY FROM er.instante),
	       	EXTRACT(DOW FROM er.instante),
		pr.distrito,pr.concelho,er.unidades
	FROM evento_reposicao er NATURAL JOIN 
		produto p NATURAL JOIN 
		instalada_em ie INNER JOIN
	       	ponto_de_retalho pr ON local=pr.nome;
