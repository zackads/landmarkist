describe('Landmarkist', () => {
  beforeEach(() => {
    cy.visit('/')
  })

  it('has a heading', () => {
    cy.get("h1");
  })
})
