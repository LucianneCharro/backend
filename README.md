## Resumo do projeto
- Projeto em andamento, sendo realizado no curso POSTECH da FIAP, visando a entrega de um sistema de streaming de vídeos, com interfaces e APIs para cadastro de Videos. Este sistema tem por finalidade criar, atualizar, listar e excluir vídeos.
Serão construidas APIs que um portal poderá consumir para apresentar aos usuários os vídeos e quais serão os favoritos.

## 🔨 Funcionalidades do projeto nessa segunda fase
- `Funcionalidade` `4 API de cadastro de videos`: A API tem como objetivo permitir o gerenciamento de informações sobre os videos cadastrados em nosso sistema. Para cadastrar um video, deve ser informada seu título, descrição, url e data de publicação e não podem estar preenchidos com brancos ou nulos. Todos os campos são obrigatórios. O sistema também deve gravar os dados no sistema.
Receber informações através do Controller em formato HTTP POST/GET/PUT/DELETE.

## ✔️ Técnicas e tecnologias utilizadas

- ``Java 18``
- ``InteliJ IDEA``
- ``Testes via INSONMIA``
- ``Injeção de depêndencias, collections, exemplo mappers, utilização do Lombok, Bean Validation, Tratamento de exceção, Enum``
- ``Persistencia Banco de dados Postgree, utilizado o Console ElephanteSQL``
- ``Para modelagem de Dados utilizamos o DataModeler``

## 🛠️ Exemplo Json/Rotas de cada API

Em anexo estão as collections do Insomnia.

1. #### Request/Response:
- `API de cadastro de vídeos`:
Request:
  {
  "titulo": "Ana",
  "descricao": "ztuw 1425",
  "url": "ztuw 1425",
  "dataPublicacao": "2023-01-21 17:54:00.00000",
  "dataCriacao": "2023-07-01 00:00:00.50000",
  "dataAlteracao": "2023-07-01 00:00:00.50000",
  "gostei": "0"
  }
Response:
  {
  Status 200 OK "Video cadastrado com sucesso"
  }

  {
  "timestamp": "2023-06-18T21:08:53.783766900Z",
  "code": 400,
  "httpStatus": "BAD_REQUEST",
  "path": "/videos"
  }
2. #### Rotas de cada API:
- `API de cadastro de videos`: http://localhost:80/videos

## 🎯 Desafios encontrados:
- Utilização do Webflux e criação do mongodb.
- 
## 📁 Acesso ao projeto
Você pode acessar os arquivos do projeto clicando [aqui](https://github.com/LucianneCharro/consumo-energia/tree/feature-segunda-fase/src).

