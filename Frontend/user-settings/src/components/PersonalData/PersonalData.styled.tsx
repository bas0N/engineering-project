import styled from "styled-components"

export const PersonalDataWrapper = styled.div`
    width: calc(90% - 20px);
    padding: 20px 10px 60px;
    display: grid;
    grid-template-columns: repeat(1, 1fr);
    column-gap: 32px;
    row-gap: 8px;
    justify-content: space-around;

    @media screen and (min-width: 425px) {
        width: calc(70% - 20px);
    }

    @media screen and (min-width: 768px){
        width: calc(60% - 20px);
        grid-template-columns: repeat(2, 1fr);
    }

    @media screen and (min-width: 1024px){
        width: calc(40% - 20px);
    }
`;