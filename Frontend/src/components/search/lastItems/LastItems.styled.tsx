import { Image, Link, Text, tokens } from "@fluentui/react-components";
import { styled } from "styled-components";

export const LastItemsWrapper = styled.section`
    width: calc(80% - 20px);
    position: relative;
    left: 10%;
    padding: 10px;
    background: #a9d3f208;
    height: calc(30vh - 20px);
    border-radius: 20px;
    box-shadow: ${tokens.shadow16};
    display: flex;
    gap: 4px;
    overflow-x: scroll;
`;

export const LastItemsItem = styled(Link)<{customBorderRadius?: string}>`
    text-decoration: none !important;
    color: white !important;
    &:focus {
        outline: none;
    }
    padding: 10px;
    min-width: calc(25vw - 20px);
    max-width: calc(25vw - 20px);
    height: calc(100% - 20px);
    text-align: center;
    ${(props) => props.customBorderRadius && `border-radius: ${props.customBorderRadius};`}
    display: flex;
    flex-direction: column;
    gap: 0px;
    cursor: pointer;
    transition: all 0.5s;
    &:hover > :nth-child(1){
        filter: brightness(70%);
    }
`;

export const LastItemsItemImage = styled(Image)`
    width: 100%;
    height: auto;
    max-height: 40%;
    width: auto;
`;

export const LastItemsItemDescription = styled.div`
    width: calc(100% - 20px);
    background: #a9d3f208;
    flex: 1;
    padding: 10px;
`;

export const LastItemsItemHeader = styled(Text).attrs({as: 'h3', size: 400})`
`;