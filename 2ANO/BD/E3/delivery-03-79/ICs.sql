-- IC 1

DROP TRIGGER IF EXISTS categorias_diferentes ON tem_outra;


CREATE OR REPLACE FUNCTION categorias_diferentes()
RETURNS TRIGGER AS
$$
BEGIN
	IF NEW.super_categoria=NEW.categoria THEN
		RAISE EXCEPTION 'Uma categoria nao pode estar contida em si propria';
	END IF;

	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER categorias_diferentes_trigger
BEFORE UPDATE OR INSERT ON tem_outra
FOR EACH ROW EXECUTE PROCEDURE categorias_diferentes();

-- IC 2

DROP TRIGGER IF EXISTS restricao_unidades_trigger ON evento_reposicao;

CREATE OR REPLACE FUNCTION restricao_unidades()
RETURNS TRIGGER AS
$$ 
BEGIN
	IF NEW.unidades > (
		SELECT unidades 
		FROM planograma
		WHERE ean=NEW.ean AND nro=NEW.nro AND num_serie=NEW.num_serie AND fabricante=NEW.fabricante) THEN
		RAISE EXCEPTION 'O numero de unidades repostas num Evento de Reposicao nao pode exceder o numero de unidades especificado no Planograma';
	END IF;
	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER restricao_unidades_trigger
BEFORE UPDATE OR INSERT ON evento_reposicao
FOR EACH ROW EXECUTE PROCEDURE restricao_unidades();

-- IC 3

DROP TRIGGER IF EXISTS restricao_produto_trigger ON evento_reposicao;

CREATE OR REPLACE FUNCTION restricao_produto()
RETURNS TRIGGER AS
$$ 

DECLARE prateleira_categoria VARCHAR;
BEGIN
	SELECT nome INTO prateleira_categoria 
	FROM prateleira 
	WHERE nro=NEW.nro AND 
		num_serie=NEW.num_serie AND
	       	fabricante=NEW.FABRICANTE;
	
	IF NOT EXISTS (
		SELECT * 
		FROM (
			SELECT * 
			FROM listar_subcategorias(prateleira_categoria) 
			UNION 
			SELECT * 
			FROM categoria 
			WHERE nome=prateleira_categoria
		) ls 
		WHERE EXISTS(
			SELECT * 
			FROM tem_categoria 
			WHERE subcategoria=nome AND 
			ean=NEW.ean
		)
	) THEN
		RAISE EXCEPTION 'Um Produto só pode ser reposto numa Prateleira que apresente (pelo menos) uma das Categorias desse Produto';
	END IF;
	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER restricao_produto_trigger
BEFORE UPDATE OR INSERT ON evento_reposicao
FOR EACH ROW EXECUTE PROCEDURE restricao_produto();

