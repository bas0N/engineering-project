import { axe, toHaveNoViolations } from "jest-axe";
import { Page404 } from "../Page404"
import { render } from "@testing-library/react";

expect.extend(toHaveNoViolations);

describe('Page 404', () => {
    it('Should have no a11y violations', async() => {
        const {container} = render(<Page404 />);
        expect(await axe(container)).toHaveNoViolations();
    })
})