export interface ModifyDeployableRequest {
    server: ServerHandle;
    deployable: DeployableReference;
}

export interface ServerHandle {
    id: string;
    type: ServerType;
}

export interface DeployableReference {
    label: string;
    path: string;
}

export interface ServerType {
    id: string;
    visibleName: string;
    description: string;
}