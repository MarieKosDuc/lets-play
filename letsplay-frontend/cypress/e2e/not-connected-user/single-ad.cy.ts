import { ServerMockNotConnectedUtils } from '../utils/servermock-not-connected.utils';

describe('Accès à une annonce', () => {
  beforeEach(() => {
    ServerMockNotConnectedUtils.homepageAdsMock();

    cy.get('[data-cy=ads]').children().first();
    cy.get('[data-cy=plus]').first().click();
  });

  it('Visits the single ad page', () => {
    cy.url().should('include', '/ad');
    cy.get('[data-cy=global-description]').should('not.exist')
  });

  it('Checks that the ad is visible', () => {
    cy.get('[data-cy=ad]').should('be.visible');
    cy.get('[data-cy=ad-title]').should('be.visible').should('contain', 'BATTEUR RECHERCHE GROUPE');
    cy.get('[data-cy=favori').should('not.exist');
  })

  it(('Checks that location, posted by, date and music styles are correctly displayed'), () => {
    cy.get('[data-cy=location]').should('be.visible').should('contain', 'Centre-Val de Loire');
    cy.get('[data-cy=posted-by]').should('be.visible').should('contain', 'Eira2');
    cy.get('[data-cy=posted-at]').should('be.visible').should('contain', '4 juillet 2024');
    cy.get('[data-cy=music-styles]').should('be.visible').should('contain', 'Symphonique,Heavy Metal,Power Metal');
  });

  it(('Checks that all ad author controls are invisible'), () => {
    cy.get('[data-cy=modifier]').should('not.exist');
  });

  it(('Tries to contact the author'), () => {
    cy.get('[data-cy=contact]').first().should('be.visible').click();
    cy.wait(1000)
    cy.get('p-toast').should('be.visible').should('contain', 'Tu dois être connecté pour répondre à une annonce');
  });

});
