package com.shinemo.wangge.core.config;


import com.shinemo.stallup.common.statemachine.InvalidStateTransitionException;
import com.shinemo.stallup.common.statemachine.SingleArcTransition;
import com.shinemo.stallup.common.statemachine.StateMachine;
import com.shinemo.stallup.common.statemachine.StateMachineFactory;
import com.shinemo.stallup.domain.enums.StallUpStatusEnum;
import com.shinemo.stallup.domain.event.StallUpEvent;
import com.shinemo.stallup.domain.request.StallUpRequest;
import com.shinemo.wangge.core.service.stallup.StallUpService;

/**
 * 摆摊状态机
 *
 * @author Chenzhe Mao
 * @date 2020-04-01
 */
public class StallUpStateMachine {

	private static StallUpService stallUpService;

	private static final StateMachineFactory<StallUpRequest, StallUpStatusEnum, StallUpEvent.StallUpEventEnum, StallUpEvent> stateMachineFactory = new StateMachineFactory<StallUpRequest, StallUpStatusEnum, StallUpEvent.StallUpEventEnum, StallUpEvent>(StallUpStatusEnum.NOT_EXIST)
		.addTransition(StallUpStatusEnum.NOT_EXIST, StallUpStatusEnum.PREPARE, StallUpEvent.StallUpEventEnum.CREATE, new CreateTransition())
		.addTransition(StallUpStatusEnum.PREPARE, StallUpStatusEnum.CANCELED, StallUpEvent.StallUpEventEnum.CANCEL, new CancelTransition())
		.addTransition(StallUpStatusEnum.PREPARE, StallUpStatusEnum.STARTED, StallUpEvent.StallUpEventEnum.SIGN, new SignTransition())
		.addTransition(StallUpStatusEnum.STARTED, StallUpStatusEnum.END, StallUpEvent.StallUpEventEnum.END, new EndTransition())
		.addTransition(StallUpStatusEnum.STARTED, StallUpStatusEnum.AUTO_END, StallUpEvent.StallUpEventEnum.AUTO_END, new AutoEndTransition())
		.addTransition(StallUpStatusEnum.PREPARE, StallUpStatusEnum.AUTO_END, StallUpEvent.StallUpEventEnum.AUTO_END, new AutoEndTransition())
		.installTopology();

	private static StateMachine<StallUpStatusEnum, StallUpEvent.StallUpEventEnum, StallUpEvent> getStateMachine(StallUpRequest request, StallUpStatusEnum stallUpType) {
		return stateMachineFactory.make(request, stallUpType);
	}

	private static StateMachine<StallUpStatusEnum, StallUpEvent.StallUpEventEnum, StallUpEvent> getStateMachine(StallUpRequest request) {
		return stateMachineFactory.make(request);
	}

	public static StallUpStatusEnum handler(StallUpRequest request, StallUpEvent stallUpEvent) throws InvalidStateTransitionException {
		StateMachine<StallUpStatusEnum, StallUpEvent.StallUpEventEnum, StallUpEvent> stallUpStateMachine = null;
		if (request.getStatus() == null) {
			stallUpStateMachine = StallUpStateMachine.getStateMachine(request);
		} else {
			stallUpStateMachine = StallUpStateMachine.getStateMachine(request, StallUpStatusEnum.getById(request.getStatus()));
		}
		StallUpStatusEnum stallUpStatusEnum = stallUpStateMachine.doTransition(stallUpEvent.getStallUpEvent(), stallUpEvent);
		return stallUpStatusEnum;
	}

	private static final class CreateTransition implements SingleArcTransition<StallUpRequest, StallUpEvent> {

		@Override
		public void transition(StallUpRequest stallUpRequest, StallUpEvent stallUpEvent) {
			if (stallUpService == null) {
				stallUpService = SpringContextHolder.getBean("stallUpService", StallUpService.class);
			}
			stallUpService.create(stallUpRequest);
		}
	}

	private static final class SignTransition implements SingleArcTransition<StallUpRequest, StallUpEvent> {

		@Override
		public void transition(StallUpRequest stallUpRequest, StallUpEvent stallUpEvent) {
			if (stallUpService == null) {
				stallUpService = SpringContextHolder.getBean("stallUpService", StallUpService.class);
			}
			stallUpService.sign(stallUpRequest);
		}
	}

	private static final class CancelTransition implements SingleArcTransition<StallUpRequest, StallUpEvent> {

		@Override
		public void transition(StallUpRequest stallUpRequest, StallUpEvent stallUpEvent) {
			if (stallUpService == null) {
				stallUpService = SpringContextHolder.getBean("stallUpService", StallUpService.class);
			}
			stallUpService.cancel(stallUpRequest);
		}
	}

	private static final class EndTransition implements SingleArcTransition<StallUpRequest, StallUpEvent> {

		@Override
		public void transition(StallUpRequest stallUpRequest, StallUpEvent stallUpEvent) {
			if (stallUpService == null) {
				stallUpService = SpringContextHolder.getBean("stallUpService", StallUpService.class);
			}
			stallUpService.end(stallUpRequest);
		}
	}

	private static final class AutoEndTransition implements SingleArcTransition<StallUpRequest, StallUpEvent> {

		@Override
		public void transition(StallUpRequest stallUpRequest, StallUpEvent stallUpEvent) {
			if (stallUpService == null) {
				stallUpService = SpringContextHolder.getBean("stallUpService", StallUpService.class);
			}
			stallUpService.autoEnd(stallUpRequest);
		}
	}
}
