import { render } from "@testing-library/react"
import { UserDetailsFrame } from "../UserDetailsFrame"
import { axe, toHaveNoViolations } from "jest-axe";
import { User } from "../../../../UsersList.helper";

expect.extend(toHaveNoViolations);

const MOCK_USER:User = {
    id: 0,
    uuid: "testUUID",
    email: "e@mail.pl",
    imageUrl: null,
    firstName: null,
    lastName: null,
    role: "",
    displayScore: 0
}

describe("User Details frame", () => {
    it('Should have no a11y violations', async() => {
        const {container} = render(<UserDetailsFrame
            user={MOCK_USER}    
        />);
        expect(await axe(container)).toHaveNoViolations();
    });
})