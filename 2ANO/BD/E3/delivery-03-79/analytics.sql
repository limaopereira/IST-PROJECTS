-- ANALYTICS 1

SELECT dia_semana,concelho, SUM(unidades)
FROM vendas 
WHERE (ano=2017 AND mes=10 AND dia_mes>=28) OR 
	(ano=2017 AND mes>10) OR
	(ano>2017 AND ano<2022) OR
	(ano=2022 AND mes=2 AND dia_mes<=21) OR 
	(ano=2022 AND mes<2)
GROUP BY CUBE (dia_semana,concelho)
ORDER BY dia_semana,concelho,SUM(unidades);

-- ANALYTICS 2

SELECT concelho,cat,dia_semana,SUM(unidades)
FROM vendas 
WHERE distrito='DISTRITO_15'
GROUP BY CUBE (concelho,cat,dia_semana)
ORDER BY concelho,cat,dia_semana,SUM(unidades);
