// ***********************************************
// This example namespace declaration will help
// with Intellisense and code completion in your
// IDE or Text Editor.
// ***********************************************
// declare namespace Cypress {
//   interface Chainable<Subject = any> {
//     customCommand(param: any): typeof customCommand;
//   }
// }
//
// function customCommand(param: any): void {
//   console.warn(param);
// }
//
// NOTE: You can use it like so:
// Cypress.Commands.add('customCommand', customCommand);
//
// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })

declare namespace Cypress {
  interface Chainable<Subject = any> {
    loginAsUser(username: string, password: string): Chainable<any>;
    loginAsAdmin(username: string, password: string): Chainable<any>;
  }
}

Cypress.Commands.add('loginAsAdmin', (username, password) => {
    cy.session([username, password], () => {
      cy.visit('/login')
      cy.get('input[type=email]').type(username);
      cy.get('input[type=password]').type(password);
      cy.get('button[type=submit]').click()
      cy.url().should('contain', '/admin/users')
    })
    cy.visit('/admin/users');
  })

  Cypress.Commands.add('loginAsUser', (username, password) => {
    cy.session([username, password], () => {
      cy.visit('/login')
      cy.get('input[type=email]').type(username);
      cy.get('input[type=password]').type(password);
      cy.get('button[type=submit]').click()
      cy.url().should('contain', '/home')
    })
    cy.visit('/home');
  })
  