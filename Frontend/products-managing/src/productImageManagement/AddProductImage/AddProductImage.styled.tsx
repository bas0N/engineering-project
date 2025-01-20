import { Button } from "@fluentui/react-components";
import { styled } from "styled-components";

export const ImagesAddingClosingButton = styled(Button).attrs({appearance: 'subtle'})`
    min-width: fit-content;
    padding: 10px;
    position: absolute;
    top: 10px;
    right: 10px;
`;

export const ImagesAddingWrapper = styled.section`
    padding: 10px;
    width: calc(100vw - 20px);
    height: calc(100vh - 20px);
    position: absolute;
    top: 0px;
    left: 0px;
    background: rgba(0,0,0,.9);
    display: flex;
    flex-direction: column;
    gap: 8px;
    align-items: center;
    z-index: 4;
`;

export const ImagesAddingFileInput = styled.input`
    opacity: 0;
    width: 0;
    padding: 0;
`;

export const ImagesAddingContainer = styled.div`
    width: calc(60% - 20px);
    padding: 10px;
    display: flex;
    flex-direction: row;
    justify-content: space-around;
    align-items: center;
    gap: 8px;
`;

export const ImagesAddingButton = styled(Button).attrs({appearance: 'subtle'})`
    min-width: fit-content !important;
    padding: 10px 20px;
`;
