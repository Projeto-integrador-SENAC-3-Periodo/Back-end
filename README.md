# Back-end

# Sistema de Gerenciamento de Atividades Complementares

Sistema acadêmico desenvolvido para gerenciar atividades complementares de alunos, permitindo o envio, avaliação e certificação de atividades extracurriculares.

## Objetivo

O sistema tem como finalidade facilitar o controle de atividades complementares em instituições de ensino, permitindo:

* Gerenciamento de usuários e permissões
* Organização de cursos e atividades
* Envio e validação de comprovantes
* Gerção de certificado de horas atingidads
* Acompanhamento de progresso acadêmico

## Atores do Sistema

### Administrador

* Cadastra usuários
* Gerencia cursos, atividades e alunos
* Vincula coordenadores e alunos aos cursos
* Visualiza logs

### Coordenador

* Gerencia cursos aos quais está vinculado
* Avalia comprovantes (aprova/reprova)
* Acompanha desempenho dos alunos

### Aluno

* Participa de cursos
* Visualiza atividades
* Envia comprovantes (imagem/PDF)
* Acompanha status, pontuação/horas aprovadas e certificados

## Regras de Acesso

* O **Administrador possui todas as permissões do sistema para gerenciamento**
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

controller/
service/
repository/
dto/
entity/
security/
config/
exception/

## Funcionalidades Implementadas

* Cadastro de usuários (restrito ao administrador)
* Login com email ou matrícula
* Autenticação com JWT
* Criptografia de senha
* Controle de acesso por perfil (ADMIN, COORDENADOR, ALUNO)
* Envio de email com senha provisória e de aviso caso o comprovante é validado ou rejeitado
* Estrutura inicial de logs do sistema
* Configuração do banco de dados e hospedado no Railway
* Integração com Spring Security
* Vinculação de coordenadores aos cursos
* Vinculação de aluno aos cursos
* Função de aprovar e reprovar comprovante
* Implementação de todos os get, put, delete,post que o sistema precisa


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

## Modelos Entidade Relacionamentos MER E MR


<img width="934" height="1401" alt="MR PI - 1 AVA" src="https://github.com/user-attachments/assets/bf315de0-b033-4349-9e11-da6d57985f59" />

<img width="2013" height="1284" alt="mer - pi 1 ava" src="https://github.com/user-attachments/assets/167e1c77-469a-4408-a3ce-897d9087f14e" />



##  Como Executar o Projeto

### Pré-requisitos

* Java 17+
* Maven
* MySQL


### Clonar o repositório

```bash
git clone https://github.com/seu-usuario/seu-repositorio.git](https://github.com/Projeto-integrador-SENAC-3-Periodo/Back-end.git)
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
* Certificado só é gerado quando o aluno atinge a quantidade de horas complementares total
* O sistema registra logs de ações dos usuários
* Notificações podem ser enviadas por email ou push


## Desenvolvido por

Abigail Maria Nazário
Projeto desenvolvido como parte do Projeto Integrador (PI) do curso de Análise e Desenvolvimento de Sistemas.

## Licença
Este projeto é acadêmico e de uso educacional.
