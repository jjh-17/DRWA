# PR 작성 가이드

## Branch 전략

![Github Flow](https://subicura.com/git/assets/img/github-flow.2fafce92.png)

1. 브랜치를 생성합니다.
2. 작업을 진행하며 `commit`을 진행합니다.
3. 로컬 저장소에서 변경된 내용을 원격 저장소로 `push` 합니다.
4. `PR`의 경우 `BackEnd`는 `dev/backend`를, `FrontEnd`는 `dev/frontend`를 target branch로 하며 최종적으로 `develop`에 합쳐집니다.
5. 변경된 내용을 가지고 `PR`을 생성합니다.
6. `PR`에서 코드 리뷰를 진행합니다.
7. 코드 리뷰가 끝나고 이상이 없을 때 `rebase` 후 `Merge` 합니다.
8. `Merge` 후 해당 브랜치에서 `pull`하고 새로운 브랜치를 생성 후 개발을 진행합니다.

### rebase 방법

``` shell
# current branch : feat/#S10P12A708-241
git rebase dev/backend
```

## Pull Request

1. 브랜치에서 내용을 수정한 뒤 `New Merge Request` 를 클릭하고 source branch와 target branch를 선택합니다.

![Create Merge Request](https://github.com/lkt9899/PS/assets/80976609/483281ef-4b7e-47eb-838c-ad136198d9de)

2. 내용을 작성 합니다.

![Write Content](https://github.com/lkt9899/PS/assets/80976609/fb5a955e-17ac-47b8-9e28-6daccc03bbe9)

> 1. 주석은 `<!-- -->`으로 작성할 수 있습니다.
> 2. `#{이슈번호}` 로 관련된 이슈의 제목을 가져올 수 있습니다.
> 3. `{Commit Hash}` 로 관련된 커밋을 가져올 수 있습니다.
> 4. `Reviewers` 로 리뷰를 원하는 인원을 선택할 수 있습니다.
> 5. `Assigness` 로 해당 `PR`에 관여한 인원을 선택할 수 있습니다.

3. 기타 설정 완료 후 PR을 생성을 완료합니다.

![Select Options](https://github.com/lkt9899/PS/assets/80976609/b7b47e5e-6b5a-48e8-8bff-a9d8694c67dd)
