import { ServerMockConnectedUserUtils } from '../utils/servermock-connected-user.utils';

describe("Contacter l'auteur d'une annonce", () => {
  beforeEach(() => {
    ServerMockConnectedUserUtils.homepageAdsMock();
    cy.loginAsUser('Test User', 'password');

    cy.get('[data-cy=ads]').children().first();
    cy.get('[data-cy=plus]').first().click();
  });

  it('Visits the single ad page', () => {
    cy.url().should('include', '/ad');
    cy.get('[data-cy=global-description]').should('not.exist')
  });

  it('Checks that the ad is visible and set to connected mode', () => {
    cy.get('[data-cy=ad]').should('be.visible');
    cy.get('[data-cy=ad-title]').should('be.visible').should('contain', 'BATTEUR RECHERCHE GROUPE');
    cy.get('[data-cy=favori').should('exist');
  })

  it(('Visits contact form, gets back to ad'), () => {
    cy.get('[data-cy=contact-author]').should('be.visible').click();
    cy.url().should('include', '/contact');
    cy.get('h1').should('contain', 'Envoie un message concernant cette annonce : ');
    cy.get('[data-cy="send-message-to-author"]').should('be.visible').should('contain', 'Envoyer').should('not.be.enabled');
    cy.get('h1').find('a').click();
    cy.url().should('include', '/ad');
  });

  it(('Fills in the form and submits it'), () => {
    cy.get('[data-cy=contact-author]').should('be.visible').click();
    cy.get('textarea').type('Bonjour, je suis intéressé par votre annonce');
    cy.get('[data-cy="send-message-to-author"]').should('be.visible').should('contain', 'Envoyer').should('be.enabled');
    cy.get('[data-cy="send-message-to-author"]').click();
    cy.get('[data-cy="message-sent"]').should('be.visible').should('contain', 'Ton message a bien été envoyé à l\'auteur de l\'annonce.');
  }); 
});