-- QUERY 1

SELECT nome
FROM retalhista NATURAL JOIN (
	SELECT tin
	FROM responsavel_por
	GROUP BY TIN
	HAVING COUNT(DISTINCT nome_cat) >= ALL (
		SELECT COUNT(DISTINCT nome_cat)
		FROM responsavel_por
		GROUP BY tin
	)
) rr;

-- QUERY 2

SELECT DISTINCT r.nome
FROM retalhista r
WHERE NOT EXISTS(
	SELECT nome
	FROM categoria_simples
	EXCEPT
	SELECT nj.nome_cat
	FROM (responsavel_por NATURAL JOIN retalhista) nj
	WHERE (nj.tin=r.tin)
);

-- QUERY 3 

SELECT ean
FROM produto p2
EXCEPT
SELECT ean
FROM evento_reposicao er;

-- QUERY 4

SELECT ean
FROM evento_reposicao
GROUP BY ean
HAVING COUNT(DISTINCT tin)=1;
