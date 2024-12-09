import { Button, Input, Text, Textarea } from '@fluentui/react-components';
import styled from 'styled-components';

export const ReviewFormWrapper = styled.section`
    width: 100vw;
    height: 100vh;
    background: #222222fa;
    position: fixed;
    top: 0;
    left: 0;
    z-index: 14;
`;

export const ReviewFormContainer = styled.section`
    height: calc(80vh - 20px);
    width: calc(80vw - 20px);
    padding: 10px;
    position: absolute;
    top: 1vh;
    left: 10vw;
    display: flex;
    flex-direction: column;
    gap: 8px;
    align-items: center;
`;

export const ReviewCloseButton = styled(Button).attrs({appearance: 'subtle'})`
    padding: 10px;
    border-radius: 10px;
    position: absolute;
    top: 10px;
    right: 10px;
`;

export const ReviewFormHeader = styled(Text).attrs({as: 'h2', align: 'center', size: 600, weight: 'semibold'})`
    padding: 10px 0px 20px;
`;

export const ReviewFormSection = styled.div`
    display: flex;
    flex-direction: row;
    gap: 8px;
    padding-bottom: 16px;
`;

export const ReviewFormTitle = styled(Input)`
    width: calc(90vw - 20px);
    padding: 10px;

    @media screen and (min-width: 768px){
        width: calc(70vw - 20px);
    }

    @media screen and (min-width: 1024px){
        width: calc(50vw - 20px);
    }
`;

export const ReviewFormOpinion = styled(Textarea).attrs({resize: 'none'})`
    width: calc(90vw - 20px);
    padding: 10px;
    min-height: calc(30vh - 20px);
    max-height: calc(40vh - 20px);

    @media screen and (min-width: 768px){
        width: calc(70vw - 20px);
    }

    @media screen and (min-width: 1024px){
        width: calc(50vw - 20px);
    }
`;

export const ReviewFormOpinionWrapper = styled.div`
    display: flex;
    flex-direction: column;
    gap: 8px;
    align-items: center;
    padding-bottom: 36px;
`;

export const ReviewFormButton = styled(Button).attrs({appearance: 'secondary'})`
    font-size: 24px;
    padding: 20px 30px;
`;
