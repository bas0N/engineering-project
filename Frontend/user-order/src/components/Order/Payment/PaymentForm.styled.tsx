import { styled } from "styled-components";

export const PaymentFormContainer = styled.div`
    padding: 16px;
    padding-top: 36px;
    border-radius: 8px;
    display: flex;
    flex-direction: column;
    gap: 12px;
    width: fit-content;
    max-width: 500px;
`;

export const PaymentCardElementWrapper = styled.div`
    max-width: 400px;
    & > * {
        width: 400px;
    }
`