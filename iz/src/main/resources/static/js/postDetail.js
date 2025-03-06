
//게시글 작성 널값 확인 및 등록
function OpenUpdateModal(message) {
    const modal = document.getElementById("postFormModal");
    const modalMessage = document.getElementById("postFormModalMessage");

    modalMessage.textContent = message;
    modal.classList.remove("hidden");

    setTimeout(CloseModal, 1000);
}

function CloseModal() {
    document.getElementById("postFormModal").classList.add("hidden");
}

function PostFormInsertBut(){
    const title = document.getElementById("title").value.trim();
    const content = document.getElementById("content").value.trim();

    if(!title){
        OpenUpdateModal("제목을 입력해주세요!")
        return false;
    }

    if(!content){
        OpenUpdateModal("게시글을 입력해주세요!")
        return false;
    }

    OpenUpdateModal("등록되었습니다!");

    setTimeout(function () {
        document.querySelector('form').submit();
    }, 1000);

    return false;
}

//날짜 형식 변경
function timeAgo(dateString) {
    const now = new Date();
    const date = new Date(dateString);

    const diffInSeconds = Math.floor((now - date) / 1000);
    const diffInMinutes = Math.floor(diffInSeconds / 60);
    const diffInHours = Math.floor(diffInMinutes / 60);
    const diffInDays = Math.floor(diffInHours / 24);

    if (diffInDays > 0) {
        return `${diffInDays}일 전`;
    } else if (diffInHours > 0) {
        return `${diffInHours}시간 전`;
    } else if (diffInMinutes > 0) {
        return `${diffInMinutes}분 전`;
    } else {
        return `방금 전`;
    }
}

$(document).ready(function() {
    const regDateList = $(".reg_date");

    $(regDateList).each((a, b) => {
        const regDate = $(b).data('reg-date');
        const timeAgoText = timeAgo(regDate);
        $(b).text(timeAgoText);
    });
});

// 댓글 작성
//댓글 입력할때 높이 조절
function autoResize(textarea) {
    // 텍스트 내용에 맞춰서 높이를 자동으로 변경
    textarea.style.height = '48px';
    textarea.style.height = (textarea.scrollHeight) + 'px';
}

function checkEnter(event) {
    if (event.key === 'Enter' && !event.shiftKey) {
        event.preventDefault();
        saveComment();
    }
}

// 더보기 기능
function toggleComment(button) {
    const textarea = button.closest('.comment-content').querySelector('textarea');
    const moreButton = button;

    if (textarea.classList.contains('overflow-hidden')) {
        textarea.classList.remove('overflow-hidden');
        textarea.style.height = textarea.scrollHeight + 'px';
        moreButton.textContent = '닫기';
    } else {
        textarea.classList.add('overflow-hidden');
        textarea.style.height = '2rem';
        moreButton.textContent = '더보기';
    }
}



// 댓글 등록시 나오는 댓글
function saveComment() {
    const content = document.getElementById('commentContent').value;
    const boardId = document.getElementById('boardIdd').value;
    const boardType = document.getElementById('commentBoardType').value;
    const commentHeight = document.getElementById('commentContent').scrollHeight;

    if (!content.trim()) {
        OpenDupdateModal("댓글을 입력하세요.");

        setTimeout(function() {
            document.getElementById("commentContent").focus();
        }, 500);

        return;
    }

    $.ajax({
        url: `/board/${boardId}/comment`,
        type: 'POST',
        data: {
            comment_content: content,
            comment_height: commentHeight,
            board_type: boardType
        },
        success: function(response) {
            if (response.success) {
                const newComment = `
                <div class="idBoard p-4 border rounded-lg bg-gray-50" style="height: ${response.comment_height}px;">
                    <div class="flex justify-between text-gray-600 text-sm font-semibold">
                        <span>${response.nickname}</span>
                        <span class="reg_date" data-reg-date="${response.reg_date}">방금 전</span>
                    </div>
                    <div class="flex justify-between items-start relative mt-2">
                        <div class="comment-content w-full" style="height: ${response.comment_height}px;">
                            <textarea class="comment-input focus:outline-none p-2 text-sm font-black w-full resize-none overflow-hidden h-8" readonly>${response.comment_content}</textarea>
                            <button type="button" class="text-blue-500 hover:text-blue-600 cursor-pointer" id="moreBtn" onclick="toggleComment(this)">
                                더보기
                            </button>
                        </div>
                    
                        <div class="w-40 text-right">
                            <button type="button" onclick="editComment(event)" class="text-blue-500 hover:text-blue-600 cursor-pointer">
                                수정
                            </button>
                            <input type="hidden" id="commentId" class="comment-id" value="${response.comment_id}"/>
                            <input type="hidden" id="boardIde" class="board-id" value="${boardId}"/>
                            <input type="hidden" id="boardTypeD" class="board-type" value="${boardType}"/>
                            <input type="hidden" id="writerId" class="writer-id" value="${response.writer_id}"/>
                            <button type="button" onclick="OpenDeleteModal(this)" class="text-red-500 hover:text-red-600 cursor-pointer">
                                삭제
                            </button>
                        </div>
                    </div>
                </div>`;

                $('.comment-list').prepend(newComment);

                $('#commentContent').val('');
                $('#commentContent').css('height', '48px');
            } else {
                alert('댓글 등록 실패!');
            }
        },
        error: function(xhr, status, error) {
            alert('서버 에러!');
        }
    });
}



// 게시글 삭제 창
function OpenDeleteModal(button) {
    selectedComment = button.closest('.idBoard');
    document.getElementById("communityDeleteModal").classList.remove("hidden");
}

function CloseDeleteModal() {
    document.getElementById("communityDeleteModal").classList.add("hidden");
    selectedComment = null;
}

// 게시글 삭제모달창
function OpenDeleteModalBoard() {
    document.getElementById("boardDeleteModal").classList.remove("hidden");
}

function CloseDeleteModalBoard() {
    document.getElementById("boardDeleteModal").classList.add("hidden");
}



//댓글 삭제
function deleteComment() {
    const commentId = selectedComment.querySelector("input[id='commentId']").value;
    const boardId = selectedComment.querySelector("input[id='boardIde']").value;
    const boardType = selectedComment.querySelector("input[id='boardTypeD']").value;
    const writerId = selectedComment.querySelector("input[id='writerId']").value;

    if (!commentId || !boardId || !boardType || !writerId) {
        alert('필요한 값 안넘어옴.');
        return;
    }

    const url = `/board/deleteComment?commentId=${commentId}&boardId=${boardId}&boardType=${boardType}&writerId=${writerId}`;

    fetch(url, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            console.log(data);

            if (data.message === "댓글이 삭제되었습니다.") {

                CloseDeleteModal();

                const selectedComment = document.querySelector(`#commentId[value='${commentId}']`).closest('.idBoard');
                if (selectedComment) {
                    selectedComment.remove();
                }

            } else {
                alert('댓글 삭제 실패!');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('서버와 통신 문제');
        });
}

//댓글 수정
function OpenDupdateModal(message) {
    const modal = document.getElementById("updateModal");
    const modalMessage = document.getElementById("alarmModalM");

    modalMessage.textContent = message;
    modal.classList.remove("hidden");

    setTimeout(CloseUpdateModal, 1000);
}

function CloseUpdateModal() {
    document.getElementById("updateModal").classList.add("hidden");
}


// 댓글 수정
function editComment(event) {
    const button = event.target;
    const commentDiv = button.closest('.idBoard');
    const moreButton = commentDiv.querySelector('#moreBtn');


    if (!commentDiv) {
        console.error('댓글 요소를 찾을 수 없습니다.');
        return;
    }

    const inputField = commentDiv.querySelector('.comment-input');
    const commentId = commentDiv.querySelector('.comment-id');
    const boardIde = commentDiv.querySelector('.board-id');
    const boardTypeD = commentDiv.querySelector('.board-type');
    const writerId = commentDiv.querySelector('.writer-id');

    if (!inputField || !commentId || !boardIde || !boardTypeD || !writerId) {
        console.error('필수 요소가 누락되었습니다.');
        return;
    }

    const originalBorder = inputField.style.border;

    inputField.removeAttribute('readonly');
    inputField.style.border = "2px solid #9bdcfd";
    inputField.style.borderTop = "none";
    inputField.style.borderLeft = "none";
    inputField.style.borderRight = "none";
    inputField.style.overflow = 'hidden';

    moreButton.textContent = '닫기'; // 더보기 버튼을 "닫기"로 변경

    setTimeout(() => {
        inputField.focus();
        const length = inputField.value.length;
        inputField.setSelectionRange(length, length);
        moreButton.textContent = '닫기';
    }, 10);

    button.innerText = '저장';


    function adjustHeight() {
        inputField.style.height = '40px';
        inputField.style.height = inputField.scrollHeight + 'px';
    }

    adjustHeight();

    inputField.addEventListener('input', adjustHeight);

    function saveEditedComment() {
        const updatedContent = inputField.value;

        if (!updatedContent.trim()) {
            OpenDupdateModal("수정할 댓글을 입력하세요.");
            inputField.focus();
            return;
        }

        fetch('/board/comment/update', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                comment_id: commentId.value,
                board_id: boardIde.value,
                board_type: boardTypeD.value,
                writerId: writerId.value,
                comment_content: updatedContent
            })
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    inputField.setAttribute('readonly', 'true');
                    inputField.style.border = originalBorder;

                    adjustHeight();

                    button.innerText = '수정';
                    button.onclick = editComment;

                    // 수정이 완료되면 다시 '더보기'로 변경
                    if (moreButton.textContent === '닫기') {
                        toggleComment(moreButton);
                    }

                    inputField.removeEventListener('keydown', handleEnterKey);

                    OpenDupdateModal("댓글이 수정됐습니다.");
                } else {
                    alert('댓글 수정 실패!');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('댓글 수정 중 오류가 발생했습니다.');
            });
    }

    function handleEnterKey(event) {
        if (event.key === 'Enter' && !event.shiftKey) {
            event.preventDefault();
            saveEditedComment();
        }
    }

    inputField.addEventListener('keydown', handleEnterKey);
    button.onclick = saveEditedComment;
}


