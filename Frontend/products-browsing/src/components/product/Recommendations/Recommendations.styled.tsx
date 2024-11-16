import styled from "styled-components";

export const RecommendationsContainer = styled.section`
    width: calc(100% - 10px);
    padding: 5px;
    display: flex;
    flex-direction: column;
    gap: 8px;
    align-items: center;
`;

export const RecommendationsWrapper = styled.div`
    width: calc(90vw - 20px);
    display: flex;
    justify-content: space-around;
    align-items: center;
    gap: 4px;
    overflow-x: scroll;
`;

export const RecommendationTile = styled.div`
    min-width: 80vw;

    @media screen and (min-width: 425px) {
        min-width: 50vw;
    }

    @media screen and (min-width: 768px){
        min-width: 35vw;
    }

    @media screen and (min-width: 1024px){
        min-width: 25vw;
    }
`;