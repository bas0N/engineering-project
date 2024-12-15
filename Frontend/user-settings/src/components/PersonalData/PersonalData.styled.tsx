import { Input } from '@fluentui/react-components';
import styled from "styled-components";

export const PersonalDataWrapper = styled.div`
    width: calc(100% - 20px);
    padding: 20px 10px 60px;
    display: flex;
    flex-direction: column;
    gap: 8px;
    align-items: center;
`;

export const PersonalDataInputs = styled.div`
    width: calc(90% - 20px);
    padding: 10px;
    display: flex;
    gap: 8px;
    justify-content: space-around;
    align-items: center;

    @media screen and (min-width: 425px) {
        width: calc(70% - 20px);
    }

    @media screen and (min-width: 768px){
        width: calc(60% - 20px);
        flex-direction: column;
    }

    @media screen and (min-width: 1024px){
        width: calc(40% - 20px);
    }
`;

export const PersonalDataInput = styled(Input)`
    width: 90%;
`;