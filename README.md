# Back-end

# Sistema de Gerenciamento de Atividades Complementares

Sistema acadêmico desenvolvido para gerenciar atividades complementares de alunos, permitindo o envio, avaliação e certificação de atividades extracurriculares.

## Objetivo

O sistema tem como finalidade facilitar o controle de atividades complementares em instituições de ensino, permitindo:

* Gerenciamento de usuários e permissões
* Organização de cursos e atividades
* Envio e validação de comprovantes
* Geração de certificados
* Acompanhamento de progresso acadêmico

## Atores do Sistema

### Administrador

* Possui acesso total ao sistema
* Cadastra usuários
* Gerencia cursos, atividades e alunos
* Vincula coordenadores aos cursos
* Visualiza logs

### Coordenador

* Gerencia cursos aos quais está vinculado
* Cria atividades
* Avalia comprovantes (aprova/reprova)
* Acompanha desempenho dos alunos

### Aluno

* Participa de cursos
* Visualiza atividades
* Envia comprovantes (imagem/PDF)
* Acompanha status, pontuação e certificados

## Regras de Acesso

* O **Administrador possui todas as permissões do sistema**
* O **Coordenador só pode atuar nos cursos aos quais está vinculado**
* O vínculo entre coordenador e curso é obrigatório para gerenciamento
* Alunos só interagem com cursos em que estão matriculados

## Tecnologias Utilizadas

* **Java 17+**
* **Spring Boot**
* **Spring Security**
* **JWT (JSON Web Token)**
* **JPA / Hibernate**
* **MySQL**
* **Lombok**
* **JavaMailSender**


## Arquitetura

O projeto segue o padrão de arquitetura em camadas:

```
controller/
service/
repository/
dto/
entity/
security/
config/
exception/
```

## Funcionalidades Implementadas

* ✅ Cadastro de usuários (restrito ao administrador)
* ✅ Login com email ou matrícula
* ✅ Autenticação com JWT
* ✅ Criptografia de senha
* ✅ Controle de acesso por perfil (ADMIN, COORDENADOR, ALUNO)
* ✅ Envio de email com senha provisória
* ✅ Estrutura inicial de logs do sistema
* ✅ Configuração do banco de dados
* ✅ Integração com Spring Security
* ✅ Vinculação de coordenadores aos cursos


## Entidades do Sistema

* Users
* Curso
* UsuarioCurso
* Atividade
* CategoriaAtividade
* TipoAtividade
* Certificado
* Notificacao
* LogSistema


## Banco de Dados

* MySQL (railway - banco em nuvem)

##  Como Executar o Projeto

### Pré-requisitos

* Java 17+
* Maven
* MySQL


### Clonar o repositório

```bash
git clone https://github.com/seu-usuario/seu-repositorio.git
```


### Executar o projeto

```bash
mvn spring-boot:run
```


## Regras de Negócio

* Apenas administradores podem cadastrar usuários
* Administradores possuem acesso total ao sistema
* Coordenadores só podem gerenciar cursos vinculados a eles
* Toda atividade deve ser avaliada
* Certificados só são gerados após aprovação
* O sistema registra logs de ações dos usuários
* Notificações podem ser enviadas por email ou push


## Desenvolvido por

Abigail Maria Nazário
Projeto desenvolvido como parte do Projeto Integrador (PI) do curso de Análise e Desenvolvimento de Sistemas.

## Licença

Este projeto é acadêmico e de uso educacional.
