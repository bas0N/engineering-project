import styled from "styled-components";

export const OrderWrapper = styled.div`
    min-width: 300px;
    margin: 0 auto;
    display: flex;
    flex-direction: column;
    gap: 8px;
    align-items: center;

    @media screen and (min-width: 425px){
        min-width: 500px;
    }
`;