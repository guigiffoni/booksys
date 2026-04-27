# BookSys
 
Sistema de gerenciamento de biblioteca com CRUD completo, índice primário por PK e relacionamento 1:N implementado com Hash Extensível.
 
---
 
## Requisitos
 
- Java 11 ou superior
Verifique com:
 
```bash
java -version
javac -version
```
 
---
 
## Compilação
 
Na raiz do projeto, execute:
 
```bash
javac src/Main.java
```
 
Ou use o script incluso:
 
```bash
bash run.sh
```
 
> O script `run.sh` remove os `.class` anteriores, recompila e já executa o servidor.
 
---
 
## Execução
 
Após compilar manualmente:
 
```bash
java src/Main
```
 
O servidor HTTP inicia na porta **8080**.
 
---
 
## Uso
 
Abra o navegador em:
 
```
http://localhost:8080/index.html
```
 
A interface permite realizar todas as operações de CRUD para cada entidade do sistema.
 
### Entidades disponíveis
 
| Entidade    | Endpoint         | Descrição                          |
|-------------|------------------|------------------------------------|
| Autor       | `/autores`       | Cadastro de autores                |
| Livro       | `/livros`        | Cadastro de livros                 |
| Usuário     | `/usuarios`      | Cadastro de usuários               |
| Empréstimo  | `/emprestimos`   | Registro de empréstimos de livros  |
 
### Operações suportadas
 
- **Inserir** — adiciona um novo registro com ID gerado automaticamente
- **Buscar** — localiza um registro pelo ID
- **Atualizar** — modifica os dados de um registro existente
- **Excluir** — realiza exclusão lógica (marcação com lápide `*`)
- **Listar** — retorna todos os registros ativos
### Relacionamento 1:N
 
O relacionamento Autor → Livros é implementado com **Hash Extensível**, permitindo buscar todos os livros de um determinado autor de forma eficiente.
 
---
 
## Persistência
 
Os arquivos de dados são criados automaticamente na pasta `data/` na primeira execução:
 
```
data/
├── autores.dat
├── livros.dat
├── usuarios.dat
└── emprestimos.dat
```
 
Os dados são **mantidos entre execuções**. Para reiniciar o banco, basta apagar os arquivos da pasta `data/`.
 
---
 
## Estrutura do Projeto
 
```
booksys/
├── src/
│   ├── Main.java               # Ponto de entrada; sobe o servidor HTTP
│   ├── controller/
│   │   └── Controller.java     # Roteamento e orquestração das requisições
│   ├── dao/
│   │   └── Dao.java            # Acesso a disco, CRUD e exclusão lógica
│   ├── model/
│   │   ├── Autor.java
│   │   ├── Livro.java
│   │   ├── Usuario.java
│   │   └── Emprestimo.java
│   ├── util/
│   │   ├── Registro.java       # Interface base dos modelos
│   │   ├── HashExtensivel.java # Implementação do Hash Extensível
│   │   ├── Balde.java          # Balde do Hash Extensível
│   │   ├── RequestHelper.java  # Utilitários de parsing de requisição
│   │   └── FormatoData.java    # Utilitário de formatação de datas
│   └── view/
│       └── index.html          # Frontend da aplicação
├── assets/
│   ├── definicao-tabelas/      # Definição dos campos de cada entidade
│   ├── diagramas/              # Diagramas de caso de uso, arquitetura e ER
│   └── definicao-projeto.odt   # Documento de definição do projeto
├── data/                       # Arquivos de dados gerados em execução
├── run.sh                      # Script de compilação e execução
└── README.md
```
 
---
 
## Erros tratados
 
- Inserção de PK duplicada
- Busca de ID inexistente
- Exclusão de registro não encontrado
- Campos obrigatórios em branco
- ISBN com formato inválido (deve ter 13 dígitos)
- Quantidade negativa de livros
