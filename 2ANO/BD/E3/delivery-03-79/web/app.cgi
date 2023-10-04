#!/usr/bin/python3
from wsgiref.handlers import CGIHandler
from flask import Flask
from flask import render_template, request
import psycopg2
import psycopg2.extras

## SGBD configs
DB_HOST = "db.tecnico.ulisboa.pt"
DB_USER = "ist198580"
DB_DATABASE = DB_USER
DB_PASSWORD = "db"
DB_CONNECTION_STRING = "host=%s dbname=%s user=%s password=%s" % (
    DB_HOST,
    DB_DATABASE,
    DB_USER,
    DB_PASSWORD,
)

app = Flask(__name__)


@app.route("/")
def list_options():
    try:
        return render_template("index.html")
    except Exception as e:
        return str(e)
     


@app.route("/categorias")
def list_options_categorias():
    try:
        return render_template("categorias.html")
    except Exception as e:
        return str(e)

@app.route("/categorias_escolha")
def escolha_categoria():
    try:
        return render_template("categorias_escolha.html")
    except Exception as e:
        return str(e)

@app.route("/categorias_inserir")
def inserir_categoria():
    try:
        return render_template("categorias_inserir.html")
    except Exception as e:
        return str(e)

@app.route("/subcategorias_inserir")
def inserir_subcategoria():
    try:
        return render_template("subcategorias_inserir.html")
    except Exception as e:
        return str(e)

@app.route("/listar_subcategorias")
def listar_subcategorias():
    try:
        return render_template("listar_subcategorias.html")
    except Exception as e:
        return str(e)

@app.route("/atualizar_listar_subcategorias", methods=["POST"])
def atualizar_listar_subcategorias():
    dbConn = None
    cursor = None
    try:
        dbConn = psycopg2.connect(DB_CONNECTION_STRING)
        cursor = dbConn.cursor(cursor_factory=psycopg2.extras.DictCursor)
        super_categoria = request.form["super_categoria"]
        query = "SELECT * FROM listar_subcategorias(%s);"
        data = (super_categoria,)
        cursor.execute(query, data)
        return render_template("lista_subcategorias.html",cursor=cursor)
    except Exception as e:
        return str(e)
    finally:
        cursor.close()
        dbConn.close()

@app.route("/atualizar_categoria", methods=["POST"])
def atualizar_categoria():
    dbConn = None
    cursor = None
    try:
        dbConn = psycopg2.connect(DB_CONNECTION_STRING)
        cursor = dbConn.cursor(cursor_factory=psycopg2.extras.DictCursor)
        nova_categoria = request.form["nova_categoria"]
        query = "SELECT inserir_categoria(%s);"
        data = (nova_categoria,)
        cursor.execute(query, data)
        return query
    except Exception as e:
        return str(e)
    finally:
        dbConn.commit()
        cursor.close()
        dbConn.close()

@app.route("/atualizar_subcategoria",methods=["POST"])
def atualizar_subcategoria():
    dbConn= None
    cursor= None
    try:
        dbConn = psycopg2.connect(DB_CONNECTION_STRING)
        cursor = dbConn.cursor(cursor_factory=psycopg2.extras.DictCursor)
        nova_subcategoria = request.form["nova_subcategoria"]
        print(nova_subcategoria)
        super_categoria = request.form["super_categoria"]
        query = "SELECT inserir_subcategoria(%s,%s);"
        data = (super_categoria,nova_subcategoria,)
        cursor.execute(query,data)
        return query
    except Exception as e:
        return str(e)
    finally:
        dbConn.commit()
        cursor.close()
        dbConn.close()

@app.route("/categorias_escolha_remover")
def remover_escolha_categoria():
    try:
        return render_template("categorias_escolha_remover.html")
    except Exception as e:
        return str(e)

@app.route("/categorias_remover")
def remover_categoria():
    try:
        return render_template("categorias_remover.html")
    except Exception as e:
        return str(e)

@app.route("/subcategorias_remover")
def remover_subcategoria():
    try:
        return render_template("subcategorias_remover.html")
    except Exception as e:
        return str(e)

@app.route("/atualizar_remover_categoria",methods=["POST"])
def atualizar_remover_categoria():
    dbConn = None
    cursor = None
    try:
        dbConn = psycopg2.connect(DB_CONNECTION_STRING)
        cursor = dbConn.cursor(cursor_factory=psycopg2.extras.DictCursor)
        categoria_remover = request.form["categoria_remover"]
        query = "SELECT remover_categoria(%s);"
        data = (categoria_remover,)
        cursor.execute(query, data)
        return query
    except Exception as e:
        return str(e)
    finally:
        dbConn.commit()
        cursor.close()
        dbConn.close()



@app.route("/atualizar_remover_subcategoria",methods=["POST"])
def atualizar_remover_subcategoria():
    dbConn = None
    cursor = None
    try:
        dbConn= psycopg2.connect(DB_CONNECTION_STRING)
        cursor= dbConn.cursor(cursor_factory=psycopg2.extras.DictCursor)
        subcategoria_remover = request.form["subcategoria_remover"]
        query = "SELECT remover_subcategoria(%s);"
        data= (subcategoria_remover,)
        cursor.execute(query,data)
        return query
    except Exception as e:
        return str(e)
    finally:
        dbConn.commit()
        cursor.close()
        dbConn.close()

@app.route("/retalhistas")
def retalhistas():
    try:
        return render_template("retalhistas.html")
    except Exception as e:
        return str(e)

@app.route("/retalhista_inserir")
def inserir_retalhistas():
    try:
        return render_template("retalhista_inserir.html")
    except Exception as e:
        return str(e)

@app.route("/acrescentar_responsabilidade")
def acrescentar_responsabilidade():
    try:
        return render_template("acrescentar_responsabilidade.html")
    except Exception as e:
        return str(e)

@app.route("/retalhista_remover")
def inserir_retalhista():
    try:
        return render_template("retalhista_remover.html")
    except Exception as e:
        return str(e)

@app.route("/atualizar_retalhista", methods=["POST"])
def atualizar_retalhista():
    dbConn = None
    cursor = None
    try:
        dbConn= psycopg2.connect(DB_CONNECTION_STRING)
        cursor= dbConn.cursor(cursor_factory=psycopg2.extras.DictCursor)
        novo_tin = request.form["novo_tin"]
        novo_nome = request.form["novo_nome"]
        query = "INSERT INTO retalhista VALUES(%s,%s);"
        data= (novo_tin,novo_nome,)
        cursor.execute(query,data)
        return query
    except Exception as e:
        return str(e)
    finally:
        dbConn.commit()
        cursor.close()
        dbConn.close()


@app.route("/atualizar_responsabilidade", methods=["POST"])
def atualizar_responsabilidade():
    dbConn = None
    cursor = None
    try:
        dbConn= psycopg2.connect(DB_CONNECTION_STRING)
        cursor= dbConn.cursor(cursor_factory=psycopg2.extras.DictCursor)
        nome_cat=request.form["nome_cat"]
        tin = request.form["tin"]
        num_serie = request.form["num_serie"]
        fabricante = request.form["fabricante"]
        query = "INSERT INTO responsavel_por VALUES(%s,%s,%s,%s);"
        data= (nome_cat,tin,num_serie,fabricante,)
        cursor.execute(query,data)
        return query
    except Exception as e:
        return str(e)
    finally:
        dbConn.commit()
        cursor.close()
        dbConn.close()

@app.route("/atualizar_remover_retalhista", methods=["POST"])
def atualizar_remover_retalhista():
    dbConn = None
    cursor = None
    try:
        dbConn= psycopg2.connect(DB_CONNECTION_STRING)
        cursor= dbConn.cursor(cursor_factory=psycopg2.extras.DictCursor)
        tin_remover = request.form["tin_remover"]
        query = "DELETE FROM retalhista WHERE tin=(%s);"
        data= (tin_remover,)
        cursor.execute(query,data)
        return query
    except Exception as e:
        return str(e)
    finally:
        dbConn.commit()
        cursor.close()
        dbConn.close()

@app.route("/eventos_reposicao")
def eventos_reposicao():
    try:
        return render_template("eventos_reposicao.html")
    except Exception as e:
        return str(e)

@app.route("/eventos_reposicao_ivm", methods=["POST"])
def eventos_reposicao_ivm():
    dbConn = None
    cursor = None
    try:
        dbConn= psycopg2.connect(DB_CONNECTION_STRING)
        cursor= dbConn.cursor(cursor_factory=psycopg2.extras.DictCursor)
        cursor2=dbConn.cursor(cursor_factory=psycopg2.extras.DictCursor)
        num_serie = request.form["num_serie"]
        fabricante = request.form["fabricante"]
        query = "SELECT * FROM evento_reposicao WHERE num_serie=(%s) AND fabricante=(%s);"
        query2 = "SELECT cat, SUM(unidades) FROM evento_reposicao NATURAL JOIN produto WHERE num_serie=(%s) AND fabricante=(%s) GROUP BY cat;"
        data= (num_serie,fabricante,)
        cursor.execute(query,data)
        cursor2.execute(query2,data)
        return render_template("eventos_reposicao_ivm.html", cursor=[cursor,cursor2])
    except Exception as e:
        return str(e)
    finally:
        cursor.close()
        dbConn.close()



CGIHandler().run(app)
