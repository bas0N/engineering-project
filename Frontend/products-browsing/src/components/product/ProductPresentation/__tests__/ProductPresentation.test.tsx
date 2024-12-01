import { axe, toHaveNoViolations } from "jest-axe";
import { render, screen } from '@testing-library/react';
import { ProductPresentation } from "../ProductPresentation";

expect.extend(toHaveNoViolations);

describe('Product Presentation Component test', () => {
    it('Should have no a11y violations', async () => {
        const {container} = render(<ProductPresentation 
            title='mockTitle'
            categories={[]}
            ratingNumber={null}
            averageRating={null}
            price='148.2' productId={""} token={""} />
        );
        expect(await axe(container)).toHaveNoViolations();
        expect(screen.getByText('product.noRatings'));
    });

    it('Should be able to display the ratings', () => {
        render(<ProductPresentation 
            title='mockTitle' 
            categories={[]} 
            ratingNumber={194} 
            averageRating={4.9} 
            price='148.2' 
            productId={""} token={""} />
        );

        expect(screen.getByText('4.9'));
        expect(screen.getByText('194'));
    })
})