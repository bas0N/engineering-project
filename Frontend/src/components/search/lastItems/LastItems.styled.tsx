import { Image, tokens } from "@fluentui/react-components";
import { styled } from "styled-components";

export const LastItemsWrapper = styled.section`
    position: relative;
    left: 1%;
    width: calc(98% - 20px);
    padding: 10px;
    background: #a9d3f208;
    height: calc(90vh - 20px);
    border-radius: 20px;
    box-shadow: ${tokens.shadow16};
    display: flex;
    flex-direction: column;
    gap: 4px;
    overflow-x: scroll;

    @media screen and (min-width: 425px){
        left: 5%;
        width: calc(90% - 20px);
    }

    @media screen and (min-width: 768px){
        height: calc(40vh - 20px);
        width: calc(80% - 20px);
        flex-direction: row;
        left: 10%;
    }
`;

export const LastItemsListContainer = styled.div`
    flex: 2;
    display: flex;
    flex-direction: column;
    gap: 4px;
    overflow: scroll;
`;

export const LastItemsListItem = styled.div<{bgOpacity: string}>`
    padding: 10px 15px;
    width: calc(100% - 30px);
    height: fit-content;
    border-radius: 12px;
    cursor: pointer;
    text-decoration: none;
    color: white;
    display: flex;
    gap: 12px;
    background: #115ea3${(props) => props.bgOpacity};
    transition: all 0.4s;
    align-items: center;

    &:hover{
        filter: brigthness(70%);
        color: white;
    }
`;

export const LastItemsRelatedContainer = styled.div`
    padding: 10px;
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;
    cursor: pointer;
`;

export const LastItemsRelatedItemImage = styled(Image)`
    max-height: 10vh;
    width: auto;
`;